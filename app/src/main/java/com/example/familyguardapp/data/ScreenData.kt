package com.example.familyguardapp.data

// Model of text data to be sent to the Cloud
data class ScreenData(
    val model : String = "claude-sonnet-4-20250514",
    val max_tokens : Int = 1000,
    val messages : List<Message>
)

data class Message(
    val role : String,
    val content : String // text data from the screen
)
