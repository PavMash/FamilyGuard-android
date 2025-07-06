package com.example.familyguardapp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// request body for /signup and /login
data class UserInfo(
    val username: String,
    val password: String
)

// response body for /signup and /login
data class ApiResponse(
    val status: String,
    val detail: String
)

// request body for /createChild
data class NewChildInfo(
    val username: String,
    val password: String,
    val child_name: String,
    val fcm_token: String // device FCM token from Firebase
)

// response body for /create-child
data class ChildResponse(
    val status: String,
    val id: String // child ID
)

// interface for login, signup, and createChild
/*
 "@Body" is used to send data classes, which will be converted to JSON
 Conversion is done by converter factory(Moshi, Gson)
 "suspend" is used for coroutines
*/
interface ApiService {
    // POST method for login
    @POST("/login")
    suspend fun login(@Body userInfo: UserInfo): Response<ApiResponse>

    // POST method for signup
    @POST("/signup")
    suspend fun signup(@Body userInfo: UserInfo): Response<ApiResponse>

    // POST method for creating a child
    @POST("/create-child")
    suspend fun createChild(@Body userInfo: NewChildInfo): Response<ChildResponse>
}
