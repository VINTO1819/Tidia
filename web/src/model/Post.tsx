import React from 'react'

export interface Post {
    WriterIP: string
    Callsign: string
    Content: string
    Emoji: ("ThumbUp" | "Funny" | "Sad" | "Angry" | "Amazing")[]
    WrittenTime: number
    Attachments: Media[]
}

export class Media {
    private readonly Type: "Youtube" | "Facebook" | "Photo"
    private readonly URL_or_TAG: string

    constructor(Type: "Youtube" | "Facebook" | "Photo", URL_or_TAG: string) {
        this.Type = Type
        this.URL_or_TAG = URL_or_TAG
    }

    toElement(): JSX.Element {
        switch (this.Type) {
            case "Youtube":
                return (
                    <div>
                        <div style={{ position: "relative" }}>
                            <a href={`https://www.youtube.com/embed/${this.URL_or_TAG}`} target='_blank'>
                                <img src={`http://img.youtube.com/vi/${this.URL_or_TAG}/0.jpg`} alt='Youtube Video' height='170' />
                            </a>
                            <div style={{ left: 70, width: "340px", bottom: 70, fontSize: "1.8em", fontWeight: "bold", position: "absolute" }}>Play ▶</div>
                        </div>
                        <br></br>
                    </div>
                )
            case "Facebook":
                return (
                    <div>
                        <div style={{ position: "relative" }}>
                            <a href={`https://www.facebook.com/watch/?v=${this.URL_or_TAG}`} target='_blank'>
                                <img src='https://www.facebook.com/images/fb_icon_325x325.png' alt='Facebook Video' height='170' />
                            </a>
                            <div style={{ left: 40, width: "340px", bottom: 65, fontSize: "1.8em", fontWeight: "bold", position: "absolute", color: "black" }}>Play ▶</div>
                        </div>
                        <br></br>
                    </div>
                )
            case "Photo":
                return (
                    <div><img src={this.URL_or_TAG} alt='Tidia Image' height='170' /><br></br></div>
                )
        }
    }


}