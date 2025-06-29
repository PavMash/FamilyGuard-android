package com.example.familyguardapp

import com.example.familyguardapp.test_classes.ApiServiceHandler
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import retrofit2.Response

class ApiServiceHandlerTest {
	private lateinit var apiService : ApiService
	private lateinit var apiServiceHandler: ApiServiceHandler

	@Before
	fun setUp() {
		apiService = mock()
		apiServiceHandler = ApiServiceHandler(apiService)
	}

	@Test
	fun loginChecker() = runBlocking {
		val userInfo = UserInfo("user", "123456")
		val generatedId = userInfo.username.plus(userInfo.password)
		val expected = ApiResponse("created", generatedId)
		whenever(apiService.login(userInfo)).thenReturn(Response.success(expected))
		val result = apiServiceHandler.loginHandler("user", "123456")
		assertEquals(result, expected)
	}

	@Test
	fun signupChecker() = runBlocking {
		val userInfo = UserInfo("username", "pizza")
		val generatedId = userInfo.username.plus(userInfo.password)
		val expected = ApiResponse("created", generatedId)
		whenever(apiService.signup(userInfo)).thenReturn(Response.success(expected))
		val result = apiServiceHandler.signupHandler("username", "pizza")
		assertEquals(result, expected)
	}

	@Test
	fun createChildChecker() = runBlocking {
		val userInfo = UserInfo("child", "monkey")
		val generatedId = userInfo.username.plus(userInfo.password)
		val expected = ApiResponse("created", generatedId)
		whenever(apiService.createChild(userInfo)).thenReturn(Response.success(expected))
		val result = apiServiceHandler.createChildHandler("child", "monkey")
		assertEquals(result, expected)
	}

	@Test
	fun errorLoginChecker() = runBlocking {
		val userInfo = UserInfo("invalid", "invalid")
		val errorResponse : Response<ApiResponse> = Response.error(401, "Unauthorized".toResponseBody())
		whenever(apiService.login(userInfo)).thenReturn(errorResponse)
		val result = apiServiceHandler.loginHandler("invalid", "invalid")
		assertEquals(result, ApiResponse("error", ""))
	}
}