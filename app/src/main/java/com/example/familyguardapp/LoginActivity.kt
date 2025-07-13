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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

//var childID: String? = null
var childName: String? = null

// this activity represents login page
class LoginActivity : AppCompatActivity() {
    companion object {
        const val CHILD_NAME = "CHILD_NAME"
    }

    private lateinit var loginButton : Button
    private var pendingEmail : String = ""
    private var pendingPassword : String = ""

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
        loginButton = findViewById<Button>(R.id.btnLogIn) // login button
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
            val intent = Intent(this, SignUpActivity::class.java) // intent to start SignUpActivity
            startActivity(intent) // starts activity
            finish() // closes LoginActivity
        }

        // sets the listener for login button
        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            val errorMessage = correctCredentials(email, password) // checks correctness of credentials
            if (errorMessage != null) { // if there is an error
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show() // notifies user about error
                return@setOnClickListener
            }
            pendingEmail = email
            pendingPassword = password
            Log.d("Pizdec", "point1")
            attemptLogin()
        }
    }

    private fun attemptLogin() {
        val networkService = NetworkService(this)
        loginButton.isEnabled = false
        lifecycleScope.launch {
            try {
                when (val result = networkService.login(pendingEmail, pendingPassword)) {
                    is NetworkResult.Success -> {
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.login_successful),
                            Toast.LENGTH_SHORT
                        ).show()

                        val usernamePrefs = getSharedPreferences("username", MODE_PRIVATE)
                        usernamePrefs.edit().putString("currentUser", pendingEmail).apply()

                        val childFlag = getSharedPreferences("child_flag", MODE_PRIVATE)
                        val childSet = childFlag.getBoolean("child_set: $pendingEmail", false)
                        if (!childSet) {
                            val addChildIntent = Intent(this@LoginActivity, AddChildActivity::class.java)
                            addChildActivityLauncher.launch(addChildIntent)
                            return@launch
                        }
                        if (childName != null) {
                            when (val resultChild =
                                networkService.createChild(pendingEmail, pendingPassword, childName!!)) {
                                is NetworkResult.Success -> {
                                    val childID = resultChild.data // gets child ID
                                    Log.d("ChildID", childID)

                                    val prefsID = getSharedPreferences("childID", MODE_PRIVATE)
                                    prefsID.edit().putString(
                                        "childID: $pendingEmail", childID).apply()
                                    Toast.makeText(
                                        this@LoginActivity,
                                        getString(R.string.login_successful),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        resultChild.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.d("childAdd", resultChild.message)
                                }
                            }
                        }

                        childName = null
                        startInstructionActivity()
                    }
                    is NetworkResult.Error -> {
                        Toast.makeText(
                            this@LoginActivity,
                            result.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                val errorText = getString(R.string.unexpected_error)
                Toast.makeText(
                    this@LoginActivity,
                    "$errorText: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            } finally {
                loginButton.isEnabled = true
            }
        }
    }

    private fun startInstructionActivity() {
        val instructionIntent = Intent(
            this@LoginActivity,
            InstructionActivity::class.java
        )
        instructionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(instructionIntent)
        finish()
    }

    // this method checks correctness of credentials
    private fun correctCredentials(email: String, password: String): String? {
        return when {
            email.isEmpty() || password.isEmpty() -> getString(R.string.fill_all_fields)
//            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.invalid_email)
//            password.length < 6 -> getString(R.string.invalid_password)
            else -> null
        }
    }

    // performs "wake up" animation with duration of 3 seconds
    private fun wakeUpView(view: View) {
        view.alpha = 0f // transparency
        view.animate().alpha(1f).setDuration(3000L).start() // makes "wake up" animation, duration: 3 seconds
    }

    private fun cancelCoroutine() {
        lifecycleScope.cancel()
    }

    private fun notifyChildNotAdded() {
        Toast.makeText(this@LoginActivity,
            getString(R.string.did_not_add_child), Toast.LENGTH_LONG).show()
    }

    private val addChildActivityLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val name = data?.getStringExtra(CHILD_NAME) ?: ""
            if (name != "") {
                childName = name
                attemptLogin()
            }
            return@registerForActivityResult
        }

}
