import Express from "express"
import mysql from "mysql2"
import dotenv from "dotenv"
import path from "path"

//for Database config
if(process.env.NODE_ENV == "dev"){
    dotenv.config({path:path.join(__dirname, "../.env.dev")})
}
const Database = mysql.createConnection({
    host:process.env.DB_HOST,
    user:process.env.DB_USER,
    password:process.env.DB_PASSWORD,
    port:parseInt(process.env.DB_PORT!!),
    database:process.env.DB_NAME
})

Database.connect()

import RTE_feed from "./route/feed"
import RTE_emoji from "./route/emoji"

const App = Express()
const cors = require('cors')
App.use(cors())
/*App.use("*", (req, res, next) => {
    res.setHeader("Access-Control-Allow-Origin", "*")
})*/
App.use("/api/feed", RTE_feed)
App.use("/api/emoji", RTE_emoji)

App.listen(9503, () => {
    console.log("Server is opened at Port 9503")
})

export {Database}
