/*
    Tidia - 당신을 위한 익명 SNS
    개발자 : VINTO

    - 이모티콘 객체 -
 */

package TidiaObjects

class Emoji(EmType:EmojiType, WriterIP:String)
{
    companion object
    {
        //이모지 타입
        enum class EmojiType
        {
            Good,
            Sad,
            Bad

        }

        //특정 이모지 개수찾기
        fun GetEmojiCount(List:MutableList<Emoji>, FindType:EmojiType):Int
        {
            var Count:Int = 0
            for(Item in List)
            {
                if(Item.EmType.toString() == FindType.toString())
                {
                    Count++
                }
            }

            return Count
        }

        //해당 IP가 해당 글에 이모지를 남겼는지 체크하고, 있으면 이전 이모지는 삭제함
        fun CheckAndRemoveOverlapEmoji(EmojiList:MutableList<Emoji>, IP:String):MutableList<Emoji>
        {
            for(EmojiItem in EmojiList)
            {
                if(EmojiItem.WriterIP == IP) //이전에 이모지를 남겼으면
                {
                    EmojiList.remove(EmojiItem)
                }
            }

            return EmojiList //기존 이모지가 삭제된 이모지 리스트를 반환
        }

    }

    var EmType:EmojiType = EmType
    var WriterIP:String = WriterIP


}