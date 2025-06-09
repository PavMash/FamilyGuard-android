package com.example.familyguardapp

import com.example.familyguardapp.data.ScreenData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// This interface sends information to the Cloud
interface CloudDispatchService {
    @POST("b0bed735-92b8-4ab6-88aa-b4dd2b777f06") // defines the endpoint

    suspend fun sendData(@Body data : ScreenData) : Response<Unit> // sends data to the Cloud
}