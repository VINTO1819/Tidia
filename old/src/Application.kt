/*
    Tidia - 당신을 위한 익명 SNS
    개발자 : VINTO

    - 서버사이드 메인 코드 -
 */

import TidiaBackup.Companion.GetBackupToBlackList
import TidiaBackup.Companion.GetBackupToPostList
import TidiaBackup.Companion.MakeBackup
import TidiaObjects.Emoji
import TidiaObjects.Emoji.Companion.GetEmojiCount
import TidiaObjects.TidiaPost
import com.retr0.ArgonWeb.*
import com.retr0.CBR.WordPair
import java.io.File
import java.net.URLDecoder
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

// 전역변수 생성 -----------------------------------
var PostList:MutableList<TidiaPost> = mutableListOf() //포스트 목록 리스트
var BlackList:MutableList<String> = mutableListOf() //차단된 유저 리스트(IP 저장)
var WebServer:ArgonHTTP = ArgonHTTP() //비동기식 서버 생성
// -------------------------------------------------

fun main(args:Array<String>)
{
    //백업된 데이터 불러오기
    PostList = GetBackupToPostList()
    BlackList = GetBackupToBlackList()

    WebServer.Init(80){
        Request, Response ->

        if(Request.RequestDirectory == "/API/Write") //API 요청(게시글 등록 등)일 경우
        {

            var Content = Request.Body.replace("Content=", "").removeRange(0..1) //콘텐츠 원본 가져오기

            //POST 객체 생성
            var Post:TidiaPost = TidiaPost(Content, mutableListOf(), mutableListOf(), Date().time, Request.RequestIP) //시험 운영이므로, 댓글은 제외
            Post = TidiaUtils.AddedAttachmentListFromPOST(Post) //첨부파일이 추가된 POST를 반환

            //각종 텍스트 필터 적용
            Post.Content = TidiaUtils.FilteringAbuse(Post.Content) //욕설 필터링
            Post.Content = TidiaUtils.TextEmojiFilter(Post.Content) //이모티콘 필터
            Post.Content = TidiaUtils.FilterXSS(Post.Content) //XSS 필터링
            Post.Content = Post.Content.replace("\r\n", "</br>") //개행기능

            Post = TidiaUtils.AddedContentCallFromPOST(Post, PostList) //언급된 사람 추가
            Post = TidiaUtils.AddedContentHyperLinkFromPOST(Post) //하이퍼링크 변환

            var RedirectHTML:String = ""
            if(Post.Content.replace("\r\n", "").replace(" ", "") != "" && TidiaPost.isHaveOverlapPOST(PostList, Post.Content) == false
                && !BlackList.contains(Request.RequestIP)) //비어있는 게시글인지, 차단된 IP는 아닌지 확인하고 중복 확인
            {
                PostList.add(0, Post) //POST 리스트에 POST 추가
                RedirectHTML = "<script>alert('등록되었습니다');\nwindow.location.href = '../../Community/';</script>"
            }
            else if(TidiaPost.isHaveOverlapPOST(PostList, Post.Content) == true) //중복되는 게시글이 있으면
            {
                RedirectHTML = "<script>alert('중복되는 게시글이 있어 업로드가 불가합니다! : (');\nwindow.location.href = '../../Community/';</script>"
            }
            else if(BlackList.contains(Request.RequestIP)) //차단된 IP가 글 등록을 요청하는 경우
            {
                RedirectHTML = "<script>alert('당신의 IP(${Request.RequestIP})는 차단된 IP입니다');\nwindow.location.href = '../../Community/';</script>"
            }
            else
            {
                RedirectHTML = "<script>alert('비어있는 게시글 또는 첨부파일만 있는 게시글은 업로드가 불가합니다!');\nwindow.location.href = '../../Community/';</script>"
            }

            Response.AddHTTPFirstHeader("200 OK")
            Response.AddHeader("Content-Type", "text/html; charset=utf-8")
            Response.AddHeader("Content-Length", RedirectHTML.toByteArray().size.toString())
            Response.AddBody(RedirectHTML.toByteArray())
            Response.ResponseToClient()

        }
        else if(Request.RequestDirectory == "/API/PostList")
        {
            var InnerHTML:String = ""
            if(PostList.size <= 0) //게시글이 없으면
            {
                InnerHTML = "<div><h1>게시글이 없습니다!</h1>\n</hr>\n<h2>당신이 게시글을 올려보는 건 어떨까요?</h2></div>"
            }
            else //게시글이 있으면 반환할 HTML에 게시글들 추가하기
            {
                for(Item in PostList)
                {
                    InnerHTML = InnerHTML + "<div class='inner columns color0' id='${Item.WrittenTime}'>\n" +
                            "<h2>${Item.Callsign} ${SimpleDateFormat("MM월 dd일 HH:mm").format(Item.WrittenTime)} (@${Item.WrittenTime.toString().removeRange(0, 7)})</h2>\n" + //작성된 시간으로 ID 작성
                            "<blockquote>${Item.Content}</blockquote>\n"

                    //첨부파일 추가
                    for(Attachment in Item.Attachments)
                        InnerHTML = InnerHTML + Attachment.toHTMLElement() //첨부파일 HTML 추가

                    //닫는 태그(이모티콘 포함)으로 마무리하기
                    InnerHTML = InnerHTML +  "<a href='javascript:SendEmoij(\"${Item.WrittenTime}\", \"Good\");' class='button'>\uD83D\uDC4D(${GetEmojiCount(Item.EmojiList, Emoji.Companion.EmojiType.Good).toString()})</a>\n" +
                            "<a href='javascript:SendEmoij(\"${Item.WrittenTime}\", \"Bad\");' class='button'>\uD83D\uDC4E(${GetEmojiCount(Item.EmojiList, Emoji.Companion.EmojiType.Bad).toString()})</a>\n" +
                            "<a href='javascript:SendEmoij(\"${Item.WrittenTime}\", \"Sad\");' class='button'>\uD83D\uDE25(${GetEmojiCount(Item.EmojiList, Emoji.Companion.EmojiType.Sad).toString()})</a>\n" +
                            "<hr />\n" +
                            "</div>\n"

                }
            }

            Response.AddHTTPFirstHeader("200 OK")
            Response.AddHeader("Content-Type", "text/plain; charset=utf-8")
            Response.AddHeader("Content-Length", InnerHTML.toByteArray().size.toString())
            Response.AddBody(InnerHTML.toByteArray())

            Response.ResponseToClient()

        }
        else if(Request.RequestDirectory.contains("/API/PostView/")) //포스트 뷰어인 경우
        {
            var PostID:String = Request.RequestDirectory.split("/")[3] //마지막 폴더 인자값을 포스트 ID로 받음
            var ReturnHTML:String = "" //반환할 문자열

            //뷰어 읽어오기
            var ResourceURL = {}::class.java.classLoader.getResource("viewer.html")
            ReturnHTML = ResourceURL.readText(Charset.forName("UTF-8"))
            var ReturnElement:String = ""

            for(PostItem in PostList)
            {
                if(PostItem.WrittenTime.toString() == PostID) //찾으려던 글인 경우
                {

                    ReturnElement = "<div class=\"inner columns color0\">\n" +
                            "<h2>${SimpleDateFormat("MM월 dd일 HH:mm").format(PostItem.WrittenTime)} (@${PostItem.WrittenTime.toString().removeRange(0, 7)})</h2>\n" + //작성된 시간으로 ID 작성
                            "<blockquote>${PostItem.Content}</blockquote>\n"

                    //첨부파일 추가
                    for(Attachment in PostItem.Attachments)
                        ReturnElement = ReturnElement + Attachment.toHTMLElement() //첨부파일 HTML 추가

                    //닫는 태그(이모티콘 포함)으로 마무리하기
                    ReturnElement = ReturnElement +  "<a href='javascript:SendEmoij(\"${PostItem.WrittenTime}\", \"Good\");' class='button'>\uD83D\uDC4D(${GetEmojiCount(PostItem.EmojiList, Emoji.Companion.EmojiType.Good).toString()})</a>\n" +
                            "<a href='javascript:SendEmoij(\"${PostItem.WrittenTime}\", \"Bad\");' class='button'>\uD83D\uDC4E(${GetEmojiCount(PostItem.EmojiList, Emoji.Companion.EmojiType.Bad).toString()})</a>\n" +
                            "<a href='javascript:SendEmoij(\"${PostItem.WrittenTime}\", \"Sad\");' class='button'>\uD83D\uDE25(${GetEmojiCount(PostItem.EmojiList, Emoji.Companion.EmojiType.Sad).toString()})</a>\n" +
                            "<hr />\n" +
                            "</div>"

                    ReturnHTML = ReturnHTML.replace("{BODY}", ReturnElement)
                }
            }

            Response.AddHTTPFirstHeader("200 OK")
            Response.AddHeader("Content-Type", "text/html; charset=utf-8")
            Response.AddHeader("Content-Length", ReturnHTML.toByteArray().size.toString())
            Response.AddBody(ReturnHTML.toByteArray())

        }
        else if(Request.RequestDirectory == "/API/Emoji") //글에 이모티콘 등록
        {
            if(!BlackList.contains(Request.RequestIP)) //차단된 IP가 아니면
            {

                var EmojiPacket:String = Request.Body.removeRange(0..1)
                var EmojiArray:Array<String> = EmojiPacket.split("|").toTypedArray()

                for(Item in PostList) //ID와 같은 글 찾기
                {
                    if(Item.WrittenTime.toString() == EmojiArray[0] && Item.WriterIP != Request.RequestIP) //ID와 같은 글이고, 해당 글의 작성자가 본인이 아니라면
                    {
                        Item.EmojiList = Emoji.CheckAndRemoveOverlapEmoji(Item.EmojiList, Request.RequestIP) //이미 이모지가 있으면 이전 이모지는 삭제
                        when(EmojiArray[1])
                        {
                            "Good" -> {Item.EmojiList.add(Emoji(Emoji.Companion.EmojiType.Good, Request.RequestIP))} //좋아요 추가
                            "Bad" -> {Item.EmojiList.add(Emoji(Emoji.Companion.EmojiType.Bad, Request.RequestIP))} //싫어요 추가
                            "Sad" -> {Item.EmojiList.add(Emoji(Emoji.Companion.EmojiType.Sad, Request.RequestIP))} //슬퍼요 추가
                        }
                    }
                }

            }
        }
        else if(Request.RequestDirectory == "/API/Chat") //AI 채팅
        {
            WhisFriend(Request, Response)
        }
        else //리소스 요청일 경우
        {

            var ResourcePath = Request.RequestDirectory.replace("/", File.separator) //일시적으로 /를 OS별 구분자로 변경
            ResourcePath = URLDecoder.decode(ResourcePath, "UTF-8")

            if(ResourcePath.removeRange(0..ResourcePath.length - 2) == File.separator)
                ResourcePath = ResourcePath + "index.html"
            ResourcePath = ResourcePath.removeRange(0, 1).replace(File.separator, "/") //Jar에서의 리소스 로딩을 위해 앞에있는 /를 없애고 OS별 경로 구분자를 리소스 로딩을 위해 /로 변경
            var ResourceURL = {}::class.java.classLoader.getResource(ResourcePath)

            Response.AddHTTPFirstHeader("200 OK")
            Response.AddHeader("Content-Type", "${MIME.GET_MIME(File(ResourcePath).extension)}; charset=utf-8")
            Response.AddHeader("Content-Length", ResourceURL.readBytes().size.toString())
            Response.AddBody(ResourceURL.readBytes())

            Response.ResponseToClient()

        }


    }

    //OpenCBR 시작
    Thread(){

        //CBR 데이터 학습
        var LearnWordList:Array<String> = {}::class.java.classLoader.getResource("WhisFriend/CBR.txt").readText(Charset.forName("UTF-8")).split("\n").toTypedArray()
        var ProcessedWord:Int = 0
        for(LearnWord in LearnWordList)
        {
            Thread(){
                try{CBREngine.AddWord(WordPair(LearnWord.split("|")[0], LearnWord.split("|")[1]))}
                catch(ex:Exception){} //학습 중 오류나면 넘어가기
                ProcessedWord++
            }.start()
        }
        while(ProcessedWord != LearnWordList.size){} //완료될 때까지 대기

        isreadyOpenCBR = true //활성화
        CBRLogText = "[${SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())}] CBR 데이터 학습 완료\n"
        val LogFile:File = File(SimpleDateFormat("yyyy_MM_dd_HH;mm").format(Date()) + ".log") //시작할 때 로그파일 선정
        LogFile.createNewFile()

        //로그 1분마다 백업
        while(true)
        {
            try{
                LogFile.writeText(CBRLogText, Charset.forName("UTF-8")) //로그 쓰기
            }catch(ex:Exception){}
            Thread.sleep(1 * 60 * 1000) //1분 대기
        }

    }.start()

    //게시글 자동삭제 스레드
    Thread(){

        while(true)
        {
            //현제시간 - 작성된 시간이(글 작성 경과시간) 삭제될 시간이거나 넘었을 떄
            PostList.removeIf{Post -> Date().time - Post.WrittenTime >= Post.DeleteHour() * 3600 * 1000}
            Thread.sleep(1 * 60 * 1000) //1분 대기
        }

    }.start()

    //게시글 및 블랙리스트 백업 스레드
    Thread(){
        while(true)
        {
            try{
                MakeBackup(PostList, BlackList) //백업 함수
            }
            catch(ex:Exception)
            {

            }

            Thread.sleep(5000) //5초마다 작동
        }

    }.start()

    //콘솔
    println(" Tidia Console")
    while(true)
    {
        //명령어 읽어오기
        print(" >>")
        var ReadCommand:String = readLine()!!
        TidiaConsole.ParseCommand(ReadCommand) //명령어 파싱 및 실행
        print("\n")
    }

}