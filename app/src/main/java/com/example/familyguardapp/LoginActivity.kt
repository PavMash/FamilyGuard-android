package com.example.familyguardapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

var childID: String? = null

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // switches to night theme

        val emailField = findViewById<EditText>(R.id.edEmail)
        val passwordField = findViewById<EditText>(R.id.edPassword)
        val loginButton = findViewById<Button>(R.id.btnLogIn)
        val signupButton = findViewById<Button>(R.id.btnSignUp)
        val imShield = findViewById<ImageView>(R.id.imShield)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)

        val text = getString(R.string.app_title)

        val spannable = SpannableString(text) // assigns different colors to different parts of text

        // green part
        spannable.setSpan(
            ForegroundColorSpan(Color.GREEN),
            0, 6,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // blue part
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            6, 11,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // red part
        spannable.setSpan(
            ForegroundColorSpan(Color.RED),
            11, 14,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvTitle.text = spannable

        // makes "wake up" animation
        wakeUpView(tvTitle)
        wakeUpView(imShield)
        wakeUpView(loginButton)
        wakeUpView(signupButton)
        wakeUpView(emailField)
        wakeUpView(passwordField)

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
                                Toast.makeText(this@LoginActivity, getString(R.string.login_successful),
                                    Toast.LENGTH_SHORT).show()
                                when (val resultChild = networkService.createChild(email,password)){
                                    is NetworkResult.Success ->{
                                        childID = resultChild.data
                                        Log.d("LoginChild", "$childID")
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
                        val errorText = getString(R.string.unexpected_error)
                        Toast.makeText(this@LoginActivity,
                            "$errorText: ${e.message}",Toast.LENGTH_LONG).show()
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
            email.isEmpty() || password.isEmpty() -> getString(R.string.fill_all_fields)
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.invalid_email)
            password.length < 6 -> getString(R.string.invalid_password)
            else -> null
        }
    }

    // performs "wake up" animation with duration of 3 seconds
    private fun wakeUpView(view : View) {
        view.alpha = 0f // transparency
        view.animate().alpha(1f).setDuration(3000L).start()
    }
}