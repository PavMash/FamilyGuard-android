package com.example.familyguardapp.test_classes

import com.example.familyguardapp.NetworkResult
import com.example.familyguardapp.NetworkServiceInterface

class NetworkServiceHandler(private val networkServiceInterface: NetworkServiceInterface) {
	suspend fun loginHandler(username : String, password : String) : String {
		val response = networkServiceInterface.login(username, password)
		when (response) {
			is NetworkResult.Success -> {
				return "Success"
			}
			is NetworkResult.Error -> {
				return "Error"
			}
		}
	}

	suspend fun signupHandler(username : String, password : String) : String {
		val response = networkServiceInterface.signup(username, password)
		when (response) {
			is NetworkResult.Success -> {
				return "Success"
			}
			is NetworkResult.Error -> {
				return "Error"
			}
		}
	}

	suspend fun createChildHandler(username : String, password : String) : String {
		val response = networkServiceInterface.createChild(username, password)
		when (response) {
			is NetworkResult.Success -> {
				return "Success"
			}
			is NetworkResult.Error -> {
				return "Error"
			}
		}
	}
}