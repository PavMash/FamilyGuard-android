package com.example.familyguardapp

interface NetworkServiceInterface {
    suspend fun login(username: String, password: String): NetworkResult<String>
    suspend fun signup(username: String, password: String): NetworkResult<String>
    suspend fun createChild(username: String, password: String): NetworkResult<String>
}
