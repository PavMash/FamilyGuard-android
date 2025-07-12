package com.example.familyguardapp.testClasses

import com.example.familyguardapp.NetworkResult
import com.example.familyguardapp.NetworkServiceInterface

// this class was created only to test NetworkService
// "networkServiceInterface" is an instance of NetworkServiceInterface
// "username" represents email
// "password" represents password(combination of characters)
// "suspend" is used for coroutines
// "when" works like "switch/case" in other programming languages
// "is" checks type of response
class NetworkServiceHandler(private val networkServiceInterface: NetworkServiceInterface) {
    // this method is used to test "login" in app
    suspend fun loginHandler(username: String, password: String): String {
        val response = networkServiceInterface.login(username, password) // call to NetworkServiceInterface
        when (response) {
            is NetworkResult.Success -> {
                return "Success" // success
            }

            is NetworkResult.Error -> {
                return "Error" // failure(error)
            }
        }
    }

    // this method is used to test "signup" in app
    suspend fun signupHandler(username: String, password: String): String {
        val response = networkServiceInterface.signup(username, password) // call to NetworkServiceInterface
        when (response) {
            is NetworkResult.Success -> {
                return "Success" // success
            }
            is NetworkResult.Error -> {
                return "Error" // failure(error)
            }
        }
    }

    // this method is used to test "child creation" in app
    suspend fun createChildHandler(username: String, password: String, childName: String): String {
        val response = networkServiceInterface.createChild(username, password, childName)
        when (response) {
            is NetworkResult.Success -> {
                return "Success" // success
            }
            is NetworkResult.Error -> {
                return "Error" // failure(error)
            }
        }
    }
}
