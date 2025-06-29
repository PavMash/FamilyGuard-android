package com.example.familyguardapp.test_classes

import com.example.familyguardapp.CloudDispatchService
import com.example.familyguardapp.ServiceResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class CloudDispatchServiceHandler(private val cloudDispatchService: CloudDispatchService) {
	suspend fun sendDataHandler(id : String, capturedText : String, format : String) : ServiceResponse? {
		val request : RequestBody = capturedText.toRequestBody(format.toMediaType())
		val response = cloudDispatchService.sendData(id, request)
		if (response.isSuccessful) {
			return response.body()
		}
		return ServiceResponse("denied", 0, "")
	}
}