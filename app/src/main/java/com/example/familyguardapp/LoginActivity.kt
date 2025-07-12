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
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

var childID: String? = null // child ID

// this activity represents login page
class LoginActivity : AppCompatActivity() {
    // this standard method creates the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // this snippet of code gets the device FCM token
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.exception) // checks error
                    return@addOnCompleteListener
                }
                fcm_token = task.result // gets FCM token
                Log.d("FCM_TOKEN", "Your token: $fcm_token") // checks logs
            }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // switches to night theme

        val emailField = findViewById<EditText>(R.id.edEmail) // email field
        val passwordField = findViewById<EditText>(R.id.edPassword) // password field
        val loginButton = findViewById<Button>(R.id.btnLogIn) // login button
        val signupButton = findViewById<Button>(R.id.btnSignUp) // signup button
        val imShield = findViewById<ImageView>(R.id.imShield) // the picture of the shield

        val tvTitle = findViewById<TextView>(R.id.tvTitle) // container for the title of the app

        val text = getString(R.string.app_title) // the title of the app

        val spannable = SpannableString(text) // assigns different colors to different parts of text

        // green part
        // sets white color for "Family" part
        spannable.setSpan(
            ForegroundColorSpan(Color.WHITE),
            0,
            6,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // blue part
        // sets green color for "Guard"
        spannable.setSpan(
            ForegroundColorSpan(Color.GREEN),
            6,
            11,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvTitle.text = spannable // sets the text to the container for it

        // makes "wake up" animation(3 seconds)
        wakeUpView(tvTitle)
        wakeUpView(imShield)
        wakeUpView(loginButton)
        wakeUpView(signupButton)
        wakeUpView(emailField)
        wakeUpView(passwordField)

        // sets the listener for signup button
        // when the button is clicked the SignUpActivity is run
        signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)// intent to start SignUpActivity
            startActivity(intent) // starts activity
            finish() // closes LoginActivity
        }

        // sets the listener for login button
        loginButton.setOnClickListener {
            val email = emailField.text.toString() // extracts email from email field
            val password = passwordField.text.toString() // extracts password from password field
            val networkService = NetworkService(this) // instance of NetworkService

            val errorMessage = correctCredentials(email, password) // checks correctness of credentials
            if (errorMessage != null) { // if there is an error
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show() // notifies user about error
                return@setOnClickListener
            } else {
                loginButton.isEnabled = false // blocks login button
                lifecycleScope.launch { // launches coroutine scope
                    try {
                        // "when" works like "switch/case" in other languages
                        when (val result = networkService.login(email, password)) { // gets the result of login
                            is NetworkResult.Success -> { // if success
                                Toast.makeText(
                                    this@LoginActivity,
                                    getString(R.string.login_successful),
                                    Toast.LENGTH_SHORT
                                ).show() // notifies user about success
                                when (val resultChild = networkService.createChild(email, password)) { // gets the result of creating a child
                                    is NetworkResult.Success -> { // if success
                                        childID = resultChild.data // gets child ID
                                        Log.d("ChildID", "$childID") // checks logs
                                    }
                                    is NetworkResult.Error -> { // if error
                                        Toast.makeText(
                                            this@LoginActivity,
                                            resultChild.message,
                                            Toast.LENGTH_SHORT
                                        ).show() // notifies user about error
                                    }
                                }
                                val intent = Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                ) // intent to start MainActivity
                                startActivity(intent) // starts activity
                                finish() // closes LoginActivity
                            }
                            is NetworkResult.Error -> { // if error
                                Toast.makeText(
                                    this@LoginActivity,
                                    result.message,
                                    Toast.LENGTH_SHORT
                                ).show() // notifies user about error
                            }
                        }
                    } catch (e: Exception) { // catches unexpected errors(for example, network error)
                        val errorText = getString(R.string.unexpected_error) // error message
                        Toast.makeText(
                            this@LoginActivity,
                            "$errorText: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show() // notifies user about error
                    } finally {
                        loginButton.isEnabled = true // enables login button
                    }
                }
            }
        }
    }

    // this method checks correctness of credentials
    private fun correctCredentials(email: String, password: String): String? {
        return when {
            email.isEmpty() || password.isEmpty() -> getString(R.string.fill_all_fields) // empty fields
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.invalid_email) // invalid email
            password.length < 6 -> getString(R.string.invalid_password) // short password
            else -> null // no error
        }
    }

    // performs "wake up" animation with duration of 3 seconds
    private fun wakeUpView(view: View) {
        view.alpha = 0f // transparency
        view.animate().alpha(1f).setDuration(3000L).start() // makes "wake up" animation, duration: 3 seconds
    }
}
