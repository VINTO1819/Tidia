/*
    Tidia - 당신을 위한 익명 SNS
    개발자 : VINTO

    - 백업 코드 -
 */

import TidiaObjects.TidiaPost
import com.google.gson.*
import java.io.File
import java.nio.charset.Charset

class TidiaBackup
{
    companion object
    {
        val BackupFilePath:String = "TidiaDATA.tid"

        //게시글 리스트 가져오기
        fun GetBackupToPostList():MutableList<TidiaPost>
        {
            var BackupFile:File = File(BackupFilePath)
            if(!BackupFile.exists())
                return mutableListOf() //백업파일이 없으면 그냥 빈 리스트 반환하기

            var ReadString:String = BackupFile.readText(Charset.forName("UTF-8"))
            var Result:MutableList<TidiaPost> = mutableListOf()
            for(Post in JsonParser().parse(ReadString).asJsonObject.get("PostArray").asJsonArray)
            {
                Result.add(Gson().fromJson(Post, TidiaPost::class.java))
            }

            for(Post in Result)
            {
                if(Post.Callsign == null)
                    Post.Callsign = "" //업데이트로 인해 Post의 Callsign이 null이면, 공백으로 전환해줌
            }

            return Result
        }

        fun GetBackupToBlackList():MutableList<String>
        {
            var BackupFile:File = File(BackupFilePath)
            if(!BackupFile.exists())
                return mutableListOf() //백업파일이 없으면 그냥 빈 리스트 반환하기

            var ReadString:String = BackupFile.readText(Charset.forName("UTF-8"))
            var BlackList:MutableList<String> = mutableListOf()

            for(Item in JsonParser().parse(ReadString).asJsonObject.get("BlackList").asJsonArray)
                BlackList.add(Item.asString) //블랙리스트 추가

            return BlackList
        }


        //백업 파일 생성
        fun MakeBackup(PostList:MutableList<TidiaPost>, BlackList:MutableList<String>)
        {
            File(BackupFilePath).createNewFile()
            var BackupJson:JsonObject = JsonObject()
            BackupJson.add("BlackList", JsonParser().parse(Gson().toJson(BlackList))) //블랙리스트 추가
            BackupJson.add("PostArray", JsonArray()) //게시글 리스트 추가

            for(Post in PostList)
            {
                BackupJson.getAsJsonArray("PostArray").add(JsonParser().parse(Gson().toJson(Post))) //포스트 추가
            }

            File(BackupFilePath).writeText(Gson().toJson(BackupJson), Charset.forName("UTF-8")) //BackupJson 저장

        }


    }
}