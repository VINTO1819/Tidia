import Base64 from "crypto-js/enc-base64"
import SHA512 from "crypto-js/sha512"
import { Media } from "./model/Post"

function PerfectReplace(Original:string, RemoveString:string, To:string):string{
    var Result = Original
    while(Result.includes(RemoveString)){
        Result = Result.replace(RemoveString, "")
    }
    return Result
}

export default class Utils{
    static Filter_Abuse(Content:string):string{
        const Filter:Array<string> = ["씨발" , "썅", "새끼", "자살", "자해", "뒤져", "찐따", "왕따", "니애미"]
        var Result = Content
        Filter.forEach(Abuse => {
            Result = PerfectReplace(Result, Abuse, Abuse.split("").map(lth => "#").join(""))
        })
        return Result
    }

    static Filter_XSS(Content:string):string{
        var Result = Content
        Result = PerfectReplace(Result, "&", "&amp;")
        Result = PerfectReplace(Result, "\\", "&quot;")
        Result = PerfectReplace(Result, "'", "&apos;")
        Result = PerfectReplace(Result, "<", "&gt;")
        Result = PerfectReplace(Result, "\\r", "<br>")
        Result = PerfectReplace(Result, "\\n", "<p>")
        return Result
    }

    static Filter_Emoji(Content:string):string{
        var Result = Content

        //My important "Easter Egg" :D
        Content.split(" ").forEach(it => {
            if(Base64.stringify(SHA512(it)) == 
            "1ec9d9f9081c5a4ed1aed594f9593c6964a03f7b6ee7f48e27ba963456213fdcb64197d1f7dbe7ba54a61b3e1297e98c263867ba87693b0abcc99cbda2282fa2"){
                Result = PerfectReplace(Result, it, `💕${it}💕`)
            }
        })

        //Others Emoji
        Result = PerfectReplace(Result, "{LOVE}","\uD83D\uDC95")
        Result = PerfectReplace(Result, "{GOOD}","\uD83D\uDC4D")
        Result = PerfectReplace(Result, "{BAD}","\uD83D\uDC4E")

        //문자형 이모티콘
        Result = PerfectReplace(Result, ":)","\uD83D\uDE03")
        Result = PerfectReplace(Result, ":(","\uD83D\uDE15")
        Result = PerfectReplace(Result, "ㅜㅜ","\uD83D\uDE25")
        Result = PerfectReplace(Result, "ㅠㅠ","\uD83D\uDE25")
        Result = PerfectReplace(Result, "lol","\uD83D\uDE01")
        Result = PerfectReplace(Result, "XD","\uD83E\uDD23")
        Result = PerfectReplace(Result, ":-*","\uD83D\uDC8B")


        return Result
    }

    static ParseAttachments(Content:string):{Content:string, Attachments:Array<Media>}{
        var Result = Content
        const Filter = new RegExp('\[([^\[\]]+)\]\(([^)]+)')
        const TagArray = Content.match(Filter)

        const Attachment:Array<Media> = []
        const TempAttachments:Array<string> = []

        TagArray?.forEach(it => {
            Result = Result.replace(it + ")", "") //remove Attachment Tag
            TempAttachments.push(it)
        })

        TempAttachments.forEach(it => {
            var Type = it.substring(1, it.length -1).split("]")[0]
            var URLorCode = it.substring(1, (Type + "])").length -1).replace("](", "")
            Attachment.push(new Media(Type as "Youtube" | "Facebook" | "Photo", URLorCode))
        })
        
        return {Content:Result, Attachments:Attachment}

    }

}