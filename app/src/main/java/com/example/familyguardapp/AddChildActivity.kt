package com.example.familyguardapp.testClasses

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.familyguardapp.LoginActivity
import com.example.familyguardapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddChildActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_child)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val childNameField = findViewById<EditText>(R.id.edChildName)
        val addChildButton = findViewById<Button>(R.id.btnAddChild)
        val backButton = findViewById<FloatingActionButton>(R.id.backButtonAddChild)

        backButton.setOnClickListener {
            val intent = Intent(this@AddChildActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
            backButton.isEnabled = false
        }

        addChildButton.setOnClickListener {
            val childName = childNameField.text.toString()
            val error = correctChildName(childName)
            if (error != "") {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sendChildNameIntent = Intent(this@AddChildActivity, LoginActivity::class.java)
            sendChildNameIntent.putExtra(LoginActivity.CHILD_NAME, childName)
            setResult(Activity.RESULT_OK, sendChildNameIntent)
            finish()
        }
    }

    private fun correctChildName(name : String) : String {
        if (name.trim().isEmpty()) {
            return getString(R.string.empty_child_name)
        }
        return ""
    }
}