import TidiaObjects.TidiaPost
import java.util.regex.Pattern

/*
    Tidia - 당신을 위한 익명 SNS
    개발자 : VINTO

    - 도구 클래스 -
 */

class TidiaUtils
{
    companion object {
        //XSS 방지 필터
        fun FilterXSS(FilterTarget: String): String {
            var Result = FilterTarget
            Result = Result.replace("&", "&amp;")
            Result = Result.replace("\\", "&quot;")
            Result = Result.replace("'", "&apos;")
            Result = Result.replace("<", "&gt;")
            Result = Result.replace("\\r", "<br>")
            Result = Result.replace("\\n", "<p>")
            return Result
        }

        //첨부파일이 포함된 POST를 반환
        fun AddedAttachmentListFromPOST(POST: TidiaPost): TidiaPost {
            var ResultPost: TidiaPost = POST

            //첨부파일 태그 변환
            var AttachmentList: MutableList<String> = mutableListOf()
            var Finder = Pattern.compile("\\[([^\\[\\]]+)\\]\\(([^)]+)").matcher(ResultPost.Content)
            while (Finder.find()) {
                var FindString = Finder.group()
                AttachmentList.add(FindString) //태그 추가하기
                ResultPost.Content = ResultPost.Content.replace(FindString + ")", "") //추가한 뒤 삭제
            }

            //첨부파일 추가
            for (Attachment in AttachmentList) {
                var Type: String = Attachment.removeRange(0, 1).split("]")[0]
                var URL: String = Attachment.removeRange(0, (Type + "](").length - 1).replace("](", "")

                ResultPost.Attachments.add(TidiaPost.MakeAttachment(Type, URL)) //첨부파일 생성 메소드로 첨부파일 생성 후 리스트에 추가

            }

            return ResultPost
        }


        //언급기능 텍스트 변환 및 강조
        fun AddedContentCallFromPOST(POST: TidiaPost, AllPost:MutableList<TidiaPost>): TidiaPost {
            var ResultPost: TidiaPost = POST

            //언급 태그 변환
            var Finder = Pattern.compile("@\\(([^)]+)").matcher(ResultPost.Content)
            var CallPeopleHTML:String = "" //언급된 사람 목록 HTML

            while (Finder.find()) {
                var FindString = Finder.group()
                ResultPost.Content = ResultPost.Content.replace(FindString + ")", "") //추가한 뒤 삭제

                for(PostItem in AllPost)
                {
                    if(PostItem.WrittenTime.toString().removeRange(0, 7) == FindString.replace("@(", "")) //언급한 사람이 실제로 있으면
                        CallPeopleHTML = CallPeopleHTML + "<strong style='color:#00FFBF;'><a href='../API/PostView/${PostItem.WrittenTime}'>@${FindString.replace("@(", "")} </a></strong>"
                }

            }

            ResultPost.Content = CallPeopleHTML + ResultPost.Content //앞에 언급된 사람 HTML 추가하기
            return ResultPost
        }


        //URL 하이퍼링크 기능
        fun AddedContentHyperLinkFromPOST(POST: TidiaPost): TidiaPost {
            var ResultPost: TidiaPost = POST

            //언급 태그 변환
            var Finder = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)").matcher(ResultPost.Content)

            while (Finder.find()) {
                var FindString = Finder.group()
                ResultPost.Content = ResultPost.Content.replace(FindString,
                    "<strong style='color:#00FFBF;'><a href='${FindString}'>${FindString}</a></strong>") //추가한 뒤 삭제
            }

            return ResultPost
        }


        //욕설 필터링 후 텍스트 반환
        fun FilteringAbuse(OriginalContent:String):String
        {
            val Filter:Array<String> = arrayOf("씨발" , "썅", "새끼", "자살", "자해", "뒤져", "찐따", "왕따", "니애미") //욕설 목록 필터
            var Result:String = OriginalContent

            //For 문으로 모든 필터링 한번씩 진행
            for(FilterItem in Filter)
            {
                var FilteredText:String = ""
                for(i in 0..FilterItem.length - 1)
                    FilteredText = FilteredText + "#" //욕설 길이만큼을 ## 문자열을 생성

                Result = Result.replace(FilterItem, FilteredText) //필터된 텍스트로 변경
            }

            return Result
        }

        //텍스트 이모티콘 필터
        fun TextEmojiFilter(OriginalContent:String):String
        {
            var Result:String = OriginalContent

            //드문 이모티콘
            Result = Result.replace("{LOVE}","\uD83D\uDC95")
            Result = Result.replace("{GOOD}","\uD83D\uDC4D")
            Result = Result.replace("{BAD}","\uD83D\uDC4E")

            //문자형 이모티콘
            Result = Result.replace(":)","\uD83D\uDE03")
            Result = Result.replace(":(","\uD83D\uDE15")
            Result = Result.replace("ㅜㅜ","\uD83D\uDE25")
            Result = Result.replace("ㅠㅠ","\uD83D\uDE25")
            Result = Result.replace("lol","\uD83D\uDE01")
            Result = Result.replace("XD","\uD83E\uDD23")
            Result = Result.replace(":-*","\uD83D\uDC8B")
            //Result = Result.replace("","")

            return Result
        }

    }
}