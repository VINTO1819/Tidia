import React, { TextareaHTMLAttributes } from 'react'
import "../assets/css/main.css"
import { Post, Media } from '../model/Post'
import faker from "faker"
import PostItem from '../components/PostItem'
import { Link } from 'react-router-dom'
import Axios from 'axios'

interface states {
    Posts: Array<Post>,
    myFeedText: string
}

export default class Feed extends React.Component<any, states>{
    componentWillMount() {
        this.setState({
            myFeedText: ""
        })

        Axios({
            url: `${window.location.protocol}//${window.location.hostname}:9503/api/feed`,
            method: "GET",
            timeout: 5000
        }).then((result) => {
            if (result.status = 200) {
                this.setState({ Posts: result.data.Feeds })
            }
        }).catch(() => {
            alert("연결에 실패하였습니다. 샘플 피드가 생성됩니다.")
            this.setState({ Posts: [] })
            for (var i = 0; i <= 30; i++) {
                var newPostArray = new Array<Post>().concat(this.state.Posts)
                newPostArray.push({
                    WriterIP: faker.internet.ip(),
                    Callsign: faker.random.number().toString(),
                    Content: faker.lorem.paragraph(),
                    Emoji: [],
                    WrittenTime: Date.now(),
                    Attachments: [new Media("Photo", faker.random.image())]
                })
                this.setState({ Posts: newPostArray })
            }
        })
    }

    render() {
        return (
            <body>
                <div id="page-wrapper" style={{ boxShadow: "10px 10px 10px 10px argb(75, 75, 75, 0.5);" }}>

                    <div id="wrapper">
                        <section className="panel" id="third">
                            <div className="intro color2">
                                <h2 className="major">Enjoy Tidia!</h2>
                                <textarea id="FeedInputBox" style={{ width: "100%", height: "100%", resize: "none", fontSize: 16 }} placeholder="아무 내용이나 작성해보세요!"
                                    onChange={(event) => { this.setState({ myFeedText: event.target.value }) }}>

                                </textarea>
                                <div style={{ width: "100%", paddingTop: 25, textAlign: "center" }}>
                                    <a onClick={() => { this.sendFeed() }} className='button'>전송 😎</a>
                                    <Link to="/help" className='button'>도움말 😏</Link>
                                </div>

                            </div>

                            <div className="inner color5" id="PostList" style={{ display: "inline-block", overflow: "auto", width: "90vh" }} >
                                {(this.state.Posts) ? this.state.Posts.map(it => <PostItem PostItem={it} />) : <h2>피드가 없습니다 :(</h2>}
                            </div>
                        </section>
                        <section className="panel" id="fourth">
                            <div className="intro color4-alt">
                                <h2 className="major">Tidia</h2>
                                <p>Tidia는 Typescript와 React로 작성된 차세대 익명 SNS입니다!</p>
                            </div>
                        </section>
                        <div className="copyright" style={{ color: "white" }}>&copy; Try on <a href="https://github.com/VINTO1819/Tidia">Github</a>.</div>
                    </div>
                </div>

                <script src="../assets/js/browser.min.js"></script>
                <script src="../assets/js/breakpoints.min.js"></script>
                <script src="../assets/js/main.js"></script>
            </body>
        )
    }

    sendFeed() {
        const Content = this.state.myFeedText
        if (this.PerfectReplace(this.PerfectReplace(Content, "\n", ""), " ", "") == "") { alert("빈 내용은 전송하실 수 없습니다") } else {
            Axios.put(`${window.location.protocol}//${window.location.hostname}:9503/api/feed`,
                { Text: Content },
                {
                    headers: {
                        "content-type": "application/json"
                    },
                    timeout: 3000
                }).then((result) => {
                    if (result.status = 200) {
                        alert("성공적으로 전송되었습니다.");
                        (document.getElementById("FeedInputBox") as any).value = ""
                        var newPostArray = new Array<Post>().concat(this.state.Posts)
                        newPostArray.push({
                            WriterIP: "",
                            Callsign: result.data.Callsign,
                            Content: Content,
                            Emoji: [],
                            WrittenTime: Date.now(),
                            Attachments: []
                        })
                        this.setState({ Posts: newPostArray })
                    }
                }).catch(() => {
                    alert("연결에 실패하였습니다.");
                })
        }
    }

    PerfectReplace(Original: string, RemoveString: string, To: string): string {
        var Result = Original
        while (Result.includes(RemoveString)) {
            Result = Result.replace(RemoveString, "")
        }
        return Result
    }


}

