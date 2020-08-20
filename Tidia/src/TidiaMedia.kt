/*
    Tidia - 당신을 위한 익명 SNS
    개발자 : VINTO

    - 미디어 업로드 코드 -
 */

class TidiaMedia(Type:MediaType, URLorTAG:String)
{
    companion object
    {
        enum class MediaType
        {
            Null,
            Youtube,
            Facebook,
            Photo
        }

    }

    var Type:MediaType = Type
    var URLorTAG:String = URLorTAG

    //파일 정보를 HTML Element로 변환
    fun toHTMLElement():String
    {
        when(Type)
        {
            Companion.MediaType.Youtube -> {
                return "<div style='position: relative;'><a href='https://www.youtube.com/embed/${URLorTAG}' target='_blank'><img src='http://img.youtube.com/vi/${URLorTAG}/0.jpg' alt='Youtube Video' height='170'></a><div style='left: 70px; width: 340px; bottom: 70px; font-size: 1.8em; font-weight: bold; position: absolute;'>Play ▶</div></div></br>"
            }
            Companion.MediaType.Photo -> {
                return "<img src='${URLorTAG}' alt='Tidia Image' height='170'></br>"
            }
            Companion.MediaType.Facebook -> {
                return "<div style='position: relative;'><a href='https://www.facebook.com/watch/?v=${URLorTAG}' target='_blank'><img src='https://www.facebook.com/images/fb_icon_325x325.png' alt='Facebook Video' height='170'></a><div style='left: 40px; width: 340px; bottom: 65px; font-size: 1.8em; font-weight: bold; position: absolute; color: black;'>Play ▶</div></div></br>"
            }
            Companion.MediaType.Null -> {return ""}
        }
    }
}