package com.example.familyguardapp

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

// This interface sends information to the Cloud
interface CloudDispatchService {
    @POST("upload-text") // defines the endpoint
    @Headers("Content-Type: text/plain")
    suspend fun sendData(
        @Query("id") id : String,
        @Body request : RequestBody
    ) : Response<Unit> // sends text data to the Cloud

    @Multipart
    @POST("upload-screenshot") // defines the endpoint
    suspend fun sendImage(
        @Part request : MultipartBody.Part
    ) : Response<Unit> // sends image data to the Cloud
}