package com.example.familyguardapp.testClasses

import com.example.familyguardapp.services.CloudDispatchService
import com.example.familyguardapp.services.ServiceResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// this class was created only to test CloudDispatchService
// "cloudDispatchService" is an instance of CloudDispatchService
class CloudDispatchServiceHandler(private val cloudDispatchService: CloudDispatchService) {
    // this method is used to test "sending data"
    // "childID" represents child ID, which is generated on server and is sent to this app
    // "capturedText" is a text captured from the child's screen
    // "format" represents the format of data(for example, "text/plain" is used to send text)
    // "ServiceResponse?" represents data class with answer("ServiceResponse" is used for json parsing)
    // "?" before "ServiceResponse" means that response body can be null
    // "RequestBody" is used to convert a text to body of HTTP request
    // "MultipartBody" is used to send a file with a text
    // "suspend" is used for coroutines
    suspend fun sendDataHandler(childID: String, capturedText: String, format: String): ServiceResponse? {
        val request: RequestBody = capturedText.toRequestBody(format.toMediaType()) // creates request to be sent
        val textPart = MultipartBody.Part.createFormData("file", "data.txt", request) // transforms "RequestBody" to "MultipartBody.Part"
        val response = cloudDispatchService.sendData(childID, textPart) // call to CloudDispatchService
        if (response.isSuccessful) {
            return response.body() // returns body in case of success
        }
        return ServiceResponse("denied", 0, "") // returns error response otherwise
    }
}
