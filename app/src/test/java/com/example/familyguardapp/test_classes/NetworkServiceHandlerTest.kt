package com.example.familyguardapp.test_classes

import com.example.familyguardapp.NetworkResult
import com.example.familyguardapp.NetworkServiceInterface
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NetworkServiceHandlerTest {
	private lateinit var networkServiceInterface: NetworkServiceInterface
	private lateinit var networkServiceHandler: NetworkServiceHandler

	@Before
	fun setUp() {
		networkServiceInterface = mock()
		networkServiceHandler = NetworkServiceHandler(networkServiceInterface)
	}

	@Test
	fun loginChecker() = runBlocking {
		val username = "user123@gmail.com"
		val password = "123456789"
		val expected = "Success"
		whenever(networkServiceInterface.login(username, password)).thenReturn(NetworkResult.Success("Success"))
		val result = networkServiceHandler.loginHandler(username, password)
		assertEquals(result, expected)
	}

	@Test
	fun signupChecker() = runBlocking {
		val username = "ma.ibragimov@innopolis.university"
		val password = "AndroidDeveloper05"
		val expected = "Success"
		whenever(networkServiceInterface.signup(username, password)).thenReturn(NetworkResult.Success("Success"))
		val result = networkServiceHandler.signupHandler(username, password)
		assertEquals(result, expected)
	}

	@Test
	fun createChildChecker() = runBlocking {
		val username = "child448@gmail.com"
		val password = "BadChild"
		val expected = "Success"
		whenever(networkServiceInterface.createChild(username, password)).thenReturn(NetworkResult.Success("Success"))
		val result = networkServiceHandler.createChildHandler(username, password)
		assertEquals(result, expected)
	}

	@Test
	fun errorLoginChecker() = runBlocking {
		val username = "invalid"
		val password = "stup1d_user"
		val expected = "Error"
		whenever(networkServiceInterface.login(username, password)).thenReturn(NetworkResult.Error("Error"))
		val result = networkServiceHandler.loginHandler(username, password)
		assertEquals(result, expected)
	}
}