import {Router} from "express"
import { Post } from "../model/Post"
import Utils from "../Utils"
import {Database} from "./../app"

const router = Router()

//get Feeds by db
router.get("/", (req, res) => {
    
})

//put Feed on db
router.put("/", (req, res) => {
    console.log(req.body)
    var Content:string = req.body.Text
    Content = Utils.Filter_XSS(Content)
    Content = Utils.Filter_Emoji(Content)

    var ParsedDATA = Utils.ParseAttachments(Content)

    const Feed:Post = {
        WriterIP:req.headers['x-forwarded-for']?.toString() || req.connection.remoteAddress!!,
        Callsign:"",
        Content:ParsedDATA.Content,
        Emoji:[],
        WrittenTime:Date.now(),
        Attachments:ParsedDATA.Attachments
    }

    Database.query(`INSERT INTO Feeds(Callsign, WriterIP, Content, WrittenTime) VALUES
    ('${Database.escape(Feed.Callsign)}', ${Feed.WriterIP}, ${Database.escape(Feed.Content)},
    ${Feed.WrittenTime})`, (err, res, field) => {

    })

    Database.query(`INSERT INTO Attachments(Callsign, Type, URLorTAG) VALUES
    ${Feed.Attachments.map(it => `(${Database.escape(Feed.Callsign)}, ${Database.escape(it.Type)}, 
    ${Database.escape(it.URL_or_TAG)})`).join(", ")}`, (err, res, field) => {

    })
    
})


export default router