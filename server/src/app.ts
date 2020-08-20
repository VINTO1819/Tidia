import Express from "express"
import CORS from "cors"
import mysql from "mysql"
import dotenv from "dotenv"
import path from "path"

//for Database config
if(process.env.NODE_ENV == "dev"){
    dotenv.config({path:path.join(__dirname, "../.env.dev")})
}

const App = Express()
App.use(CORS)
App.use("api/feed")
App.use("api/emoji")
