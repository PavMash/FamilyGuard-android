package com.example.familyguardapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val passwordField = findViewById<EditText>(R.id.editTextTextPassword)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<Button>(R.id.signUpButtonFromLogin)

        signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val networkService = NetworkService(this)

            val errorMessage = correctCredentials(email, password)
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                loginButton.isEnabled = false
                lifecycleScope.launch {
                    try {
                        when(val result = networkService.login(email,password)){
                            is NetworkResult.Success -> {
                                Toast.makeText(this@LoginActivity, "Login successful",
                                    Toast.LENGTH_SHORT).show()
                                var childID: String? = null
                                when (val resultChild = networkService.createChild(email,password)){
                                    is NetworkResult.Success ->{
                                        childID = resultChild.data
                                    }
                                    is NetworkResult.Error -> {
                                        Toast.makeText(this@LoginActivity,
                                            resultChild.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                                val intent = Intent(this@LoginActivity,
                                    MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            is NetworkResult.Error -> {
                                Toast.makeText(this@LoginActivity, result.message,
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    catch (e: Exception){
                        Toast.makeText(this@LoginActivity,
                            "Unexpected error: ${e.message}",Toast.LENGTH_LONG).show()
                    }
                    finally {
                        loginButton.isEnabled = true
                    }
                }
            }

        }
    }

    private fun correctCredentials(email: String, password: String): String? {
        return when {
            email.isEmpty() || password.isEmpty() -> "Fill all fields"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email"
            password.length < 6 -> "Password length must be bigger than five"
            else -> null
        }
    }

}