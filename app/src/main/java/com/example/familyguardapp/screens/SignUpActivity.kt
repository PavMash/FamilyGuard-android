package com.example.familyguardapp.screens

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.familyguardapp.R
import com.example.familyguardapp.services.NetworkResult
import com.example.familyguardapp.services.NetworkService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val emailField = findViewById<EditText>(R.id.edSignEmail)
        val passwordField = findViewById<EditText>(R.id.edSignPassword)
        val signUpButton = findViewById<Button>(R.id.btnSignSignUp)
        val backButton = findViewById<FloatingActionButton>(R.id.backButton)
        val networkService = NetworkService(this)

        backButton.imageTintList = ColorStateList.valueOf(Color.BLACK) // makes arrow black

        backButton.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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
                        when (val response = networkService.signup(email, password)) {
                            is NetworkResult.Success -> {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    getString(R.string.signup_successful),
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            is NetworkResult.Error -> {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    response.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        val errorText = getString(R.string.unexpected_error)
                        Toast.makeText(
                            this@SignUpActivity,
                            "$errorText: " +
                                "${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    } finally {
                        signUpButton.isEnabled = true
                    }
                }
            }
        }
    }

    private fun correctCredentials(email: String, password: String): String? {
        return when {
            email.isEmpty() || password.isEmpty() -> getString(R.string.fill_all_fields)
//            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.invalid_email)
//            password.length < 6 -> getString(R.string.invalid_password)
            else -> null
        }
    }
}
