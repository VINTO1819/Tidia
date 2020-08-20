/*
    Tidia - 당신을 위한 익명 SNS
    개발자 : VINTO

    - 게시글 객체 -
 */

package TidiaObjects
import TidiaMedia

class TidiaPost(Content:String, Attachments:MutableList<TidiaMedia>, EmojiList:MutableList<Emoji>, WrittenTime:Long, WriterIP:String, Callsign:String = "")
{
    var Content = Content
    var Attachments = Attachments
    var EmojiList = EmojiList
    var WrittenTime = WrittenTime
    var WriterIP = WriterIP
    var Callsign = Callsign

    fun DeleteHour():Int //삭제해야 할 시간 반환
    {
        var AllHour:Int = 24
        if(EmojiList.size in 5..10) //반응이 5~10개라면
        {
            AllHour = AllHour + 6 //30시간
        }
        else if(EmojiList.size in 10..15) //반응이 10~15개라면
        {
            AllHour = AllHour + 10 //34시간
        }
        else if(EmojiList.size > 15) //15개 이상이면
        {
            AllHour = AllHour + 24 //48시간
        }

        return AllHour
    }

    companion object
    {
        // 타입과 URL을 받아 첨부파일 반환
        fun MakeAttachment(Type:String, URL:String):TidiaMedia
        {
            when(Type) //추가하기
            {
                "IMG" -> {return TidiaMedia(TidiaMedia.Companion.MediaType.Photo, URL)} //웹 URL 이미지
                "YOUTUBE" -> {return TidiaMedia(TidiaMedia.Companion.MediaType.Youtube, URL)} //유튜브 영상
                "FACEBOOK" -> {return TidiaMedia(TidiaMedia.Companion.MediaType.Facebook, URL)} //페이스북 영상
                else -> {return TidiaMedia(TidiaMedia.Companion.MediaType.Null, "")}
            }
        }

        //게시글 중에서 이 글과 중복되는 글이 있는지 확인하기
        fun isHaveOverlapPOST(POSTList:MutableList<TidiaPost>, POSTContent:String):Boolean
        {
            for(Item in POSTList)
            {
                if(Item.Content == POSTContent)
                {
                    return true
                }
            }

            return false
        }


    }


}