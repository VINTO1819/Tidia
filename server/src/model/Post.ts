export interface Post {
    WriterIP: string
    Callsign: string
    Content: string
    Emoji: ("ThumbUp" | "Funny" | "Sad" | "Angry" | "Amazing")[]
    WrittenTime: number
    Attachments: Media[]
}

export class Media {
    readonly Type: "Youtube" | "Facebook" | "Photo"
    readonly URL_or_TAG: string

    constructor(Type: "Youtube" | "Facebook" | "Photo", URL_or_TAG: string) {
        this.Type = Type
        this.URL_or_TAG = URL_or_TAG
    }
}