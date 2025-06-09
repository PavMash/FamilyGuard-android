package com.example.familyguardapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.familyguardapp.data.ScreenData
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {
    // Retrofit instance
    private val retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://webhook.site/") // base url without endpoint
            .addConverterFactory(MoshiConverterFactory.create()) // converter for JSON
            .build()
    }

    // instance of a service, which will send requests
    private val cloudDispatchService by lazy {
        retrofit.create(CloudDispatchService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Launches suspend function in non-suspend function
        lifecycleScope.launch {
            testFunc()
        }
    }

    private suspend fun testFunc() {
        val data = ScreenData("something")
        val response = cloudDispatchService.sendData(data)
        if (response.isSuccessful) {
            Log.d("MyLog", "Success")
        }
        else {
            Log.d("MyLog", "Fail")
        }
    }
}