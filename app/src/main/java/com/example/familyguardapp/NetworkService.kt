package com.example.familyguardapp

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class NetworkService(private val context: Context) : NetworkServiceInterface {
    fun createOkHttpClient(): OkHttpClient {
        val certInputStream: InputStream = context.resources.openRawResource(R.raw.server_cert)
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificate = certificateFactory.generateCertificate(certInputStream)
        certInputStream.close()

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", certificate)

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagerFactory.trustManagers, null)

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManagerFactory.trustManagers[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://5.129.222.88/")
        .client(createOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override suspend fun login(username: String, password: String): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(UserInfo(username, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    when (body?.status) {
                        "OK" -> NetworkResult.Success(body.detail)
                        "failed" -> NetworkResult.Error(body.detail)
                        else -> NetworkResult.Error("Unknown response")
                    }
                } else {
                    NetworkResult.Error("Network error: ${response.code()}")
                }
            } catch (e: Exception) {
                NetworkResult.Error("Connection failed: ${e.message}")
            }
        }
    }

    override suspend fun signup(username: String, password: String): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val fcmResponse = apiService.uploadToken(FCMInfo(fcm_token!!))
                if (fcmResponse.isSuccessful) {
                    Log.d("fcmResponse", "Response code: ${fcmResponse.code()}")
                    Log.d("fcmResponse", "Password: ${fcmResponse.body()?.password}")
                }
                val response = apiService.signup(UserInfo(username, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    when (body?.status) {
                        "OK" -> NetworkResult.Success(body.detail)
                        "failed" -> NetworkResult.Error(body.detail)
                        else -> NetworkResult.Error("Unknown response")
                    }
                } else {
                    NetworkResult.Error("Network error: ${response.code()}")
                }
            } catch (e: Exception) {
                NetworkResult.Error("Connection failed: ${e.message}")
            }
        }
    }

    override suspend fun createChild(username: String, password: String, childName: String): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("childAdd", childName.toString())
                val generatedFCMToken = "user"
                    .plus((1..1_000_000).random())
                    .plus((1..1_000_000).random())
                    .plus(System.currentTimeMillis())
                val chInf = NewChildInfo(username, password, childName)
                val response = apiService.createChild(chInf)
                Log.d("childAdd", chInf.toString())
                Log.d("childAdd", "status: ${response.body()?.status}")
                if (response.isSuccessful) {
                    val body = response.body()
                    when (body?.status) {
                        "created" -> NetworkResult.Success(body.detail)
                        "failed" -> NetworkResult.Error(body.detail)
                        else -> NetworkResult.Error("Unknown response")
                    }
                } else {
                    NetworkResult.Error("Network error: ${response.code()}")
                }
            } catch (e: Exception) {
                NetworkResult.Error("Connection failed: ${e.message}")
            }
        }
    }

    override suspend fun sendUsageReport(report: UsageReport): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.sendUsageReport(report)
                if (response.isSuccessful) {
                    NetworkResult.Success("Usage report sent")
                } else {
                    NetworkResult.Error("Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                NetworkResult.Error("Error: ${e.message}")
            }
        }
    }

}

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String) : NetworkResult<T>()
}
