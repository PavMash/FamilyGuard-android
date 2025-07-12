package com.example.familyguardapp.testClasses

import com.example.familyguardapp.ApiResponse
import com.example.familyguardapp.ApiService
import com.example.familyguardapp.ChildResponse
import com.example.familyguardapp.NewChildInfo
import com.example.familyguardapp.UserInfo

class ApiServiceHandler(private val apiService: ApiService) {
    suspend fun loginHandler(username: String, password: String): ApiResponse? {
        val userInfo = UserInfo(username, password)
        val response = apiService.login(userInfo)
        if (response.isSuccessful) {
            return response.body()
        }
        return ApiResponse("error", "")
    }

    suspend fun signupHandler(username: String, password: String): ApiResponse? {
        val userInfo = UserInfo(username, password)
        val response = apiService.signup(userInfo)
        if (response.isSuccessful) {
            return response.body()
        }
        return ApiResponse("error", "")
    }

    suspend fun createChildHandler(username: String, password: String, childName: String): ChildResponse? {
        val userInfo = NewChildInfo(username, password, childName)
        val response = apiService.createChild(userInfo)
        if (response.isSuccessful) {
            return response.body()
        }
        return ChildResponse("error", "")
    }
}
