import React from 'react'
import "../assets/css/main.css"
import { Post } from '../model/Post'
import moment from "moment"
import Axios from "axios"

interface props {
    PostItem: Post
}

interface states {
    ThumbUp: number
    Funny: number
    Sad: number
    Angry: number
    Amazing: number
    SelectedEmoji: ("ThumbUp" | "Funny" | "Sad" | "Angry" | "Amazing")[]
}

export default class PostItem extends React.Component<props, states>{
    componentWillMount() {
        this.setState({
            ThumbUp: 0,
            Funny: 0,
            Sad: 0,
            Angry: 0,
            Amazing: 0,
            SelectedEmoji: []
        })
    }

    render() {
        return (
            <div className="inner columns color0 card">
                <h2>#{this.props.PostItem.Callsign} | {moment(this.props.PostItem.WrittenTime).format('dddd h시 mm분')}</h2>
                <blockquote>{this.props.PostItem.Content}</blockquote>
                {this.props.PostItem.Attachments.map(it => it.toElement())}

                <a onClick={() => (this.state.SelectedEmoji.includes("ThumbUp")) ? this.deleteEmoji("ThumbUp") : this.sendEmoji("ThumbUp")} className='button'>👍({this.state.ThumbUp})</a>
                <a onClick={() => (this.state.SelectedEmoji.includes("Funny")) ? this.deleteEmoji("Funny") : this.sendEmoji("Funny")} className='button'>🤣({this.state.Funny})</a>
                <a onClick={() => (this.state.SelectedEmoji.includes("Sad")) ? this.deleteEmoji("Sad") : this.sendEmoji("Sad")} className='button'>😥({this.state.Sad})</a>
                <a onClick={() => (this.state.SelectedEmoji.includes("Angry")) ? this.deleteEmoji("Angry") : this.sendEmoji("Angry")} className='button'>😠({this.state.Angry})</a>
                <a onClick={() => (this.state.SelectedEmoji.includes("Amazing")) ? this.deleteEmoji("Amazing") : this.sendEmoji("Amazing")} className='button'>😮({this.state.Amazing})</a>
            </div>
        )
    }

    applyValue(Key:"none" | "ThumbUp" | "Funny" | "Sad" | "Angry" | "Amazing", HowTo:"Plus" | "Minus"){
        var Object = JSON.parse(JSON.stringify(this.state))
        if(HowTo == "Plus"){Object[Key]++ ; Object["SelectedEmoji"].push(Key)}else{Object[Key]-- ; Object["SelectedEmoji"] = Object["SelectedEmoji"].filter((it:string) => it != Key)}
        this.setState(Object as states)
    }
    
    sendEmoji(Type: "ThumbUp" | "Funny" | "Sad" | "Angry" | "Amazing") {
        this.applyValue(Type, "Plus")
        Axios({
            url: `${window.location.protocol}//${window.location.hostname}:9503/api/emoji/${this.props.PostItem.Callsign}`,
            method: "PUT",
            data: { EmojiType: Type }
        }).then(() => {
            
        })
    }

    deleteEmoji(Type: "ThumbUp" | "Funny" | "Sad" | "Angry" | "Amazing") {
        this.applyValue(Type, "Minus")
        Axios({
            url: `${window.location.protocol}//${window.location.hostname}:9503/api/emoji/${this.props.PostItem.Callsign}`,
            method: "DELETE",
            data: { EmojiType: Type }
        }).then(() => {

        })
    }

}

