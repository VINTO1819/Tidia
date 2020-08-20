/*
    Tidia - 당신을 위한 익명 SNS
    개발자 : VINTO

    - MIME 체크 코드 -
 */

class MIME
{
    companion object{

        public fun GET_MIME(Extension:String):String
        {
            var ChangedText:String = Extension.toLowerCase()
            when(ChangedText)
            {
                //미디어
                "jpg", "jpeg" -> return "image/jpg"
                "png" -> return "image/png"
                "txt" -> return "text/plain"

                //웹
                "js" -> return "application/js"
                "html", "htm" -> return "text/html"
                "css" -> return "text/css"
                "xml" -> return "text/xml"

                //기타 바이너리
                else -> return "application/octet-stream"


            }

        }



    }
}

