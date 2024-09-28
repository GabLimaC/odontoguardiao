package com.example.odontoguardio

import DatabaseManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class RecoverpassActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var btnProximo: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var dbManager: DatabaseManager
    private lateinit var btnback: Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recoverpass)

        etEmail = findViewById(R.id.etEmail)
        btnProximo = findViewById(R.id.btnProximo)
        progressBar = findViewById(R.id.progress_bar)
        btnback = findViewById(R.id.btnBack)
        dbManager = DatabaseManager()

        btnProximo.setOnClickListener {
            recoverPassword()
        }

        btnback.setOnClickListener{
            finish()
        }

    }

    private fun recoverPassword() {
        val email = etEmail.text.toString().trim()

        // Validate email
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(applicationContext, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return
        }

        // Show ProgressBar and disable user interaction
        progressBar.visibility = View.VISIBLE
        disableUserInteraction()

        lifecycleScope.launch {
            try {
                // Generate a recovery code
                val recoveryCode = generateRecoveryCode()

                // Store the recovery code associated with the user's email
                val userExists = dbManager.storeRecoveryCode(email, recoveryCode)

                withContext(Dispatchers.Main) {
                    if (userExists) {
                        // Send the recovery email
                        val emailSent = dbManager.sendRecoveryEmail(email, recoveryCode)
                        if (emailSent) {
                            Toast.makeText(applicationContext, "Recovery code sent to your email.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, ConfirmationCodeActivity::class.java).apply {
                                putExtra("email", email)
                            }
                            startActivity(intent)

                            // Navigate to the next activity if needed
                        } else {
                            Toast.makeText(applicationContext, "Failed to send recovery email.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Email not found.", Toast.LENGTH_SHORT).show()
                    }
                    // Hide ProgressBar and enable user interaction
                    progressBar.visibility = View.GONE
                    enableUserInteraction()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Hide ProgressBar and enable user interaction
                    progressBar.visibility = View.GONE
                    enableUserInteraction()
                    Toast.makeText(applicationContext, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Function to generate a random recovery code
    private fun generateRecoveryCode(): String {
        val charset = ('A'..'Z') + ('0'..'9')
        return List(6) { charset.random() }.joinToString("")
    }

    // Function to send recovery email using Mailjet


    // Disable user interaction
    private fun disableUserInteraction() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    // Enable user interaction
    private fun enableUserInteraction() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}