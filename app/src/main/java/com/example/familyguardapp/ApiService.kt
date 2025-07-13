package com.example.familyguardapp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// request body for /signup and /login
data class UserInfo(
    val username: String,
    val password: String
)

// response body for /signup, /login, and /create-child
data class ApiResponse(
    val status: String,
    val detail: String
)

// request body for /createChild
data class NewChildInfo(
    val username: String,
    val password: String,
    val child_name: String
)

// request body for /upload-token
data class FCMInfo(
    val token: String // FCM token
)

// response body for /upload-token
data class FCMResponse(
    val password: String // password to deactivate token
)
data class UsageReport(
    val child_id: String,
    val date: String,
    val total_screen_time_minutes: Int,
    val top_apps: List<AppUsage>
)

data class AppUsage(
    val app_name: String,
    val minutes_used: Int
)

// interface for /login, /signup, and /create-child
// "@Body" is used to send data classes, which will be converted to JSON
// Conversion is done by converter(Moshi, Gson)
// "suspend" is used for coroutines
interface ApiService {
    // POST method for login
    @POST("/login")
    suspend fun login(@Body userInfo: UserInfo): Response<ApiResponse>

    // POST method for signup
    @POST("/signup")
    suspend fun signup(@Body userInfo: UserInfo): Response<ApiResponse>

    // POST method for creating a child
    @POST("/create-child")
    suspend fun createChild(@Body userInfo: NewChildInfo): Response<ApiResponse>

    @POST("/upload-token")
    suspend fun uploadToken(@Body fcmInfo: FCMInfo): Response<FCMResponse>

    @POST("/report-usage")
    suspend fun sendUsageReport(@Body report: UsageReport): Response<ApiResponse>
}
