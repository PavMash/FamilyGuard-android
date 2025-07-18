package com.example.familyguardapp.testClasses

import com.example.familyguardapp.services.ApiResponse
import com.example.familyguardapp.services.ApiService
import com.example.familyguardapp.services.NewChildInfo
import com.example.familyguardapp.services.UserInfo

// this class was created only to test ApiService
// "apiService" is an instance of ApiService
// "username" represents email
// "password" represents password(combination of characters)
// "ApiResponse?" represents data class with answer("ApiResponse" is used for json parsing)
// "?" before "ApiResponse" means that response body can be null
// "UserInfo" is used for "/signup", "/login" and "/create-child" requests
// "suspend" is used for coroutines
class ApiServiceHandler(private val apiService: ApiService) {
    // this method is used to test "login" in API
    suspend fun loginHandler(username: String, password: String): ApiResponse? {
        val userInfo = UserInfo(username, password) // example of user data for login
        val response = apiService.login(userInfo) // call to ApiService
        if (response.isSuccessful) {
            return response.body() // returns body in case of success
        }
        return ApiResponse("error", "") // returns error response otherwise
    }

    // this method is used to test "signup" in API
    suspend fun signupHandler(username: String, password: String): ApiResponse? {
        val userInfo = UserInfo(username, password) // example of user data for signup
        val response = apiService.signup(userInfo) // call to ApiService
        if (response.isSuccessful) {
            return response.body() // returns body in case of success
        }
        return ApiResponse("error", "") // returns error response otherwise
    }

    // this method tests "child creation" method in API
    suspend fun createChildHandler(username: String, password: String): ApiResponse? {
        val userInfo = NewChildInfo(username, password, "Mark") // example of user data for creating a child
        val response = apiService.createChild(userInfo) // call to ApiService
        if (response.isSuccessful) {
            return response.body() // returns body in case of success
        }
        return ApiResponse("error", "") // returns error response otherwise
    }
}
