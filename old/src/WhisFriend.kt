import com.retr0.ArgonWeb.ArgonRequest
import com.retr0.ArgonWeb.ArgonResponse
import com.retr0.CBR.*
import java.text.SimpleDateFormat
import java.util.*

var isreadyOpenCBR = false //OpenCBR 열려있는지 확인
var CBREngine:OpenCBR = OpenCBR()
var CBRLogText = "" //CBR 로그

fun WhisFriend(Req: ArgonRequest, Res: ArgonResponse)
{
    if(isreadyOpenCBR == true)
    {
        //OPENCBR 엔진이 준비되면
        var RequestText:String = Req.Body.replace("\r\n","").replace("Text=", "")
        RequestText = TidiaUtils.FilteringAbuse(RequestText) //욕설 필터링
        RequestText = RequestText.replace("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "") //특수문자 제거

        var Result = CBREngine.Parse(RequestText)
        var ResultWeight:MutableList<Float> = mutableListOf()
        for(Item in Result)
            ResultWeight.add(Item.Weight)

        var MaxCount:Float = 0.0f
        for(i in ResultWeight)
        {
            if(i > MaxCount)
            {
                MaxCount = i
            }
        }

        ResultWeight.removeIf(){it != MaxCount}
        check@for(Item in Result)
        {
            if(Item.Weight == ResultWeight[0])
            {
                Res.AddHTTPFirstHeader("200 OK")
                Res.AddHeader("Content-Type", "text/plain; charset=utf-8")
                Res.AddHeader("Content-Length", Item.Word.toByteArray().size.toString())
                Res.AddBody(Item.Word.toByteArray())
                Res.ResponseToClient()

                //동작 로그
                CBRLogText = CBRLogText + "[${SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())}] 요청 : '${RequestText}' / 반환 : '${Item.Word}(${Item.Weight})'\r\n"
                break@check
            }

        }

        if(Result.size == 0) //결과가 안나왔다면
        {
            //동작 로그
            CBRLogText = CBRLogText + "[${SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())}] 요청 : '${RequestText}' / 반환 : '결과값 없음'\r\n"
        }




    }
}