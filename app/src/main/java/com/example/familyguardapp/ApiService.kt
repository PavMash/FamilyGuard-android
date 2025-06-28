package com.example.familyguardapp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class UserInfo (
    val username : String,
    val password : String
)
data class ApiResponse(
    val status : String,
    val detail : String
)

interface ApiService {
    @POST("/login")
    suspend fun login(@Body userInfo: UserInfo): Response<ApiResponse>

    @POST("/signup")
    suspend fun signup(@Body userInfo: UserInfo): Response<ApiResponse>

    @POST("/create-child")
    suspend fun createChild(@Body userInfo: UserInfo): Response<ApiResponse>
}
