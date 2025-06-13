package com.example.familyguardapp.data

// Model of image data to be sent to the Cloud
data class ImageData(
    val model: String = "claude-sonnet-4-20250514",
    val max_tokens: Int = 1024,
    val messages: List<ImageMessage>
)

data class ImageMessage(
    val role: String = "user",
    val content: List<ContentItem>
)

data class ContentItem(
    val type: String,
    val text: String? = null,
    val source: ImageSource? = null
)

data class ImageSource(
    val type: String = "base64",
    val media_type: String = "image/png",
    val data: String // image from the screen
)
