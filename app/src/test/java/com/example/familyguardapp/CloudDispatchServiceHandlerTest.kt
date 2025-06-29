package com.example.familyguardapp

import com.example.familyguardapp.test_classes.CloudDispatchServiceHandler
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.mock
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import retrofit2.Response

class CloudDispatchServiceHandlerTest {
	private lateinit var cloudDispatchService: CloudDispatchService
	private lateinit var cloudDispatchServiceHandler : CloudDispatchServiceHandler

	@Before
	fun setUp() {
		cloudDispatchService = mock()
		cloudDispatchServiceHandler = CloudDispatchServiceHandler(cloudDispatchService)
	}

	@Test
	fun sendDataChecker() = runBlocking {
		val capturedText = "Drugs"
		val childID = "123"
		val format = "text/plain"
		val expected = ServiceResponse("accepted", 100, "yes")
		whenever(cloudDispatchService.sendData(eq(childID), argThat { requestBody ->
			val buffer = Buffer()
			requestBody.writeTo(buffer)
			buffer.readUtf8() == capturedText
		})).thenReturn(Response.success(expected))
		val result = cloudDispatchServiceHandler.sendDataHandler(childID, capturedText, format)
		assertEquals(result, expected)
	}

	@Test
	fun errorChecker() = runBlocking {
		val capturedText = "wrong"
		val childID = ""
		val format = "text/plain"
		val errorMessage = "Child id is empty"
		val errorResponse : Response<ServiceResponse> = Response.error(401, errorMessage.toResponseBody())
		whenever(cloudDispatchService.sendData(eq(childID), argThat { requestBody ->
			val buffer = Buffer()
			requestBody.writeTo(buffer)
			buffer.readUtf8() == capturedText
		})).thenReturn(errorResponse)
		val result = cloudDispatchServiceHandler.sendDataHandler(childID, capturedText, format)
		assertEquals(result, ServiceResponse("denied", 0, ""))
	}
}