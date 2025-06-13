package com.example.familyguardapp

import com.example.familyguardapp.data.ImageData
import com.example.familyguardapp.data.ScreenData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// This interface sends information to the Cloud
interface CloudDispatchService {
    @POST("messages") // defines the endpoint
    suspend fun sendData(
        @Header("Authorization") authHeader: String,
        @Header("Anthropic-Version") version: String = "2023-06-01",
        @Body request : ScreenData
    ) : Response<Unit> // sends text data to the Cloud

    @POST("messages") // defines the endpoint
    suspend fun sendImage(
        @Header("Authorization") authHeader : String,
        @Header("Anthropic-Version") version: String = "2023-06-01",
        @Body request : ImageData
    ) : Response<Unit> // sends image data to the Cloud
}