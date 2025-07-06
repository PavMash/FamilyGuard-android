package com.example.familyguardapp

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

// encapsulates service response body parameters
data class ServiceResponse(
    val status: String,
    val size: Int,
    val block: String
)

// This interface sends information to the Cloud
interface CloudDispatchService {
    @Multipart
    @POST("upload-text") // defines the endpoint
    suspend fun sendData(
        @Query("id") id: String,
        @Part text: MultipartBody.Part
    ): Response<ServiceResponse> // sends text data to the Cloud

    @Multipart
    @POST("upload-screenshot") // defines the endpoint
    suspend fun sendImage(
        @Part request: MultipartBody.Part
    ): Response<Unit> // sends image data to the Cloud
}
