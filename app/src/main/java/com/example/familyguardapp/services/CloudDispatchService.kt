package com.example.familyguardapp.services

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

// "ServiceResponse" represents response body for "/upload-text"
// encapsulates service response body parameters
data class ServiceResponse(
    val status: String,
    val size: Int,
    val block: String // contains confirmation or refusal whether to block the screen
)

// This interface sends information to the server
// "@Multipart" is needed to send file
// "suspend" is used for coroutines
interface CloudDispatchService {
    @Multipart
    @POST("upload-text") // defines the endpoint
    // this method sends text to the server
    suspend fun sendData(
        @Query("child_id") id: String, // child ID
        @Part("app_name") appName : String,
        @Part text: MultipartBody.Part // file with text captured from the child's screen
    ): Response<ServiceResponse> // sends text data to the Cloud
}
