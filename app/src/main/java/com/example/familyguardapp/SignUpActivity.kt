package com.example.familyguardapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val emailField = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val passwordField = findViewById<EditText>(R.id.editTextTextPassword2)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val backButton = findViewById<Button>(R.id.backButton)
        val networkService = NetworkService(this)

        backButton.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            backButton.isEnabled = false
        }

        signUpButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            val errorMessage = correctCredentials(email, password)
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                signUpButton.isEnabled = false
                lifecycleScope.launch {
                    try {
                        when (val response = networkService.signup(email, password)){
                            is NetworkResult.Success -> {
                                Toast.makeText(this@SignUpActivity, "Signup successful",
                                    Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@SignUpActivity, LoginActivity
                                ::class.java)
                                startActivity(intent)
                                finish()
                            }
                            is NetworkResult.Error -> {
                                Toast.makeText(this@SignUpActivity, response.message,
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@SignUpActivity,"Unexpected error: " +
                                "${e.message}", Toast.LENGTH_LONG).show()
                    }
                    finally {
                        signUpButton.isEnabled = true
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