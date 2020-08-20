import {Router} from "express"

const router = Router()

router.get("/:Callsign", (req, res) => {
    console.log(req.params.Callsign + "에 대한 이모티콘 추가 요청")
})

router.put("/:Callsign", (req, res) => {
    console.log(req.params.Callsign + "에 대한 이모티콘 추가 요청")
})

router.delete("/:Callsign", (req, res) => {
    console.log(req.params.Callsign + "에 대한 이모티콘 삭제 요청")
})

export default router