package com.example.familyguardapp.test_classes

import com.example.familyguardapp.ApiResponse
import com.example.familyguardapp.ApiService
import com.example.familyguardapp.ChildResponse
import com.example.familyguardapp.NewChildInfo
import com.example.familyguardapp.UserInfo

class ApiServiceHandler(private val apiService: ApiService) {
	suspend fun loginHandler(username : String, password : String) : ApiResponse? {
		val userInfo = UserInfo(username, password)
		val response = apiService.login(userInfo)
		if (response.isSuccessful) {
			return response.body()
		}
		return ApiResponse("error", "")
	}

	suspend fun signupHandler(username : String, password : String) : ApiResponse? {
		val userInfo = UserInfo(username, password)
		val response = apiService.signup(userInfo)
		if (response.isSuccessful) {
			return response.body()
		}
		return ApiResponse("error", "")
	}

	suspend fun createChildHandler(username : String, password : String) : ChildResponse? {
		val userInfo = NewChildInfo(username, password, "Mark", "some_token")
		val response = apiService.createChild(userInfo)
		if (response.isSuccessful) {
			return response.body()
		}
		return ChildResponse("error", "")
	}
}