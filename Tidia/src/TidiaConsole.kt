import TidiaObjects.TidiaPost
import kotlin.system.exitProcess

class TidiaConsole
{
    companion object
    {

        //명령어 파싱
        fun ParseCommand(Command:String)
        {
            var SplitCommand:Array<String> = Command.split(" ").toTypedArray() //띄어쓰기로 분할
            when(SplitCommand[0]) //명령어 종류
            {
                "?" -> {
                    println(" Tidia Console 명령어 모음\n" +
                            " ◎ View\n" +
                            " ◎ Del\n" +
                            " ◎ Kick\n" +
                            " ◎ Callsign\n" +
                            " ◎ Close")
                }

                "View" -> { //View 명령어 : 게시글 출력
                    if(SplitCommand.size == 1) //인자값이 없으면
                    {
                        println(" View : 인자값이 없습니다. 아래와 같이 사용하세요.")
                        println("  * View <열람할 게시글의 Index> : 특정 게시글을 열람합니다.")
                        println("  * View -all : 모든 게시글을 열람합니다.")
                        return
                    }

                    when(SplitCommand[1])
                    {
                        "-all" -> { //모두 열람
                            for(PostIndex in 0..PostList.size -1)
                                println("\n ID : ${PostList[PostIndex].Callsign}${PostList[PostIndex].WrittenTime}(Index ${PostIndex})\n IP : ${PostList[PostIndex].WriterIP}\n 내용 : '${PostList[PostIndex].Content.replace("\r\n", " ")}'")

                        }
                        else -> { //Index 열람
                            try{
                                var Post:TidiaPost = PostList[SplitCommand[1].toInt()] //포스트 가져오기
                                println(" ID : ${SplitCommand[1].toInt()}${Post.WrittenTime}(Index ${SplitCommand[1]})\n IP : ${Post.WriterIP}\n 내용 : '${Post.Content.replace("\r\n", " ")}'")
                            }
                            catch(ex:Exception){println(" ${SplitCommand[1]}번째 인덱스의 글 조회를 실패하였습니다. 인자값이 숫자거나 글이 존재하는지 확인하세요.")}
                        }
                    }

                }

                "Del" -> { //Del 명령어 : 게시글 삭제
                    if(SplitCommand.size == 1) //인자값이 없으면
                    {
                        println(" Del : 인자값이 없습니다. 아래와 같이 사용하세요.")
                        println("  * Del <삭제할 게시글의 Index>")
                        return
                    }

                    try {
                        PostList.removeAt(SplitCommand[1].toInt())
                        println(" Index ${SplitCommand[1]}의 글이 삭제되었습니다.")
                    }
                    catch(ex:Exception){println(" 해당 글을 삭제할 수 없습니다. 존재하거나 인덱스가 정수인지 확인하세요.")}
                }

                "Kick" -> { //Kick 명령어 : 사용자 차단
                    if(SplitCommand.size == 1) //인자값이 없으면
                    {
                        println(" Kick : 인자값이 없습니다. 아래와 같이 사용하세요.")
                        println("  * Kick -add <차단할 IP> : 해당 IP 차단")
                        println("  * Kick -remove <차단 해제할 IP> : 해당 IP 차단 해제")
                        println("  * Kick -list : 차단된 IP 목록")
                        return
                    }

                    when(SplitCommand[1])
                    {
                        "-add" -> {
                            if(SplitCommand.size == 3)
                            {
                                for(KickIP in BlackList)
                                {
                                    if(KickIP == SplitCommand[2] || SplitCommand[2] == "")
                                    {
                                        println(" '${SplitCommand[2]}'은/는 이미 차단된 IP이거나 IP가 아닙니다.")
                                        return
                                    }
                                }
                                println(" '${SplitCommand[2]}'이/가 차단 목록에 추가되었습니다.")
                                BlackList.add(SplitCommand[2])
                            }
                            else{println(" 인자값이 없습니다.")}

                        }
                        "-remove" -> {
                            if(SplitCommand.size == 3) {
                                for(KickIP in BlackList)
                                {
                                    if(KickIP == SplitCommand[2])
                                    {
                                        BlackList.remove(SplitCommand[2])
                                        println(" '${SplitCommand[2]}'이/가 차단 해제되었습니다.")
                                        return
                                    }
                                }
                                println(" '${SplitCommand[2]}'를 찾을 수 없습니다.")
                            }
                            else{println(" 인자값이 없습니다.")}

                        }
                        "-list" -> {
                            if(BlackList.size == 0)
                            {
                                println(" 차단된 IP가 없습니다.")
                            }
                            else
                            {
                                for(KickIP in BlackList)
                                    println(" 차단된 IP : '${KickIP}'")
                            }
                        }
                    }

                }

                "Callsign" -> { //식별 부호 수정

                    if(SplitCommand.size < 3) //인자값이 없으면
                    {
                        println(" Callsign : 인자값이 없습니다. 아래와 같이 사용하세요.")
                        println("  * Callsign <게시글 인덱스> <식별 부호> : 식별 부호 커스텀")
                        println("  * Callsign <게시글 인덱스> Crown : 공지글 지정")
                        println("  * Callsign <게시글 인덱스> Clear : Callsign 삭제")
                        return
                    }

                    if (SplitCommand.size == 3) {
                        try{
                            PostList[SplitCommand[1].toInt()].Callsign = SplitCommand[2].replace("Clear", "").replace("Crown", "\uD83D\uDC51")
                            println(" Index ${SplitCommand[1]}의 Callsign은 '${PostList[SplitCommand[1].toInt()].Callsign}'입니다.")
                        }
                        catch(ex:Exception){println(" 인자값이 올바르지 않습니다.")}

                    } else {
                        println(" 인자값이 없습니다.")

                    }

                }

                "Close" -> { //Close 명령어 : 서버 종료
                    println(" 서버를 종료합니다.")
                    WebServer.Close()
                    exitProcess(0)
                }

                else -> {println(" '${SplitCommand[0]}'은/는 존재하지 않는 명령어입니다.")}

            }
        }
    }
}