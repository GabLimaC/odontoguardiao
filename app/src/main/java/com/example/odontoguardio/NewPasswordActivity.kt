package com.example.odontoguardio

import DatabaseManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewPasswordActivity : AppCompatActivity() {
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSubmitNewPassword: Button
    private lateinit var btnBack: ImageButton

    private lateinit var progressBar: ProgressBar
    private lateinit var dbManager: DatabaseManager
    private lateinit var utilidade: Utilidade
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        // Initialize UI elements
        etNewPassword = findViewById(R.id.etNovaSenha)
        etConfirmPassword = findViewById(R.id.etRepitaSenha)
        btnSubmitNewPassword = findViewById(R.id.btnProximo)
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progress_bar)
        dbManager = DatabaseManager()
        utilidade = Utilidade()

        // Retrieve the email from Intent
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        userEmail = sharedPref.getString("userEmail", null)

        //veryfy satate
        if (userEmail == null){
            Toast.makeText(this, "Algo deu errado, por favor faça login novamente", Toast.LENGTH_LONG).show()

            val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                clear()
                apply()
            }

            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Setup OnClickListener for submit button
        btnSubmitNewPassword.setOnClickListener {
            submitNewPassword()
        }

        btnBack.setOnClickListener{
            finish()
        }

    }

    private fun submitNewPassword() {
        val newPassword = etNewPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        // Validate inputs
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return
        }

        // Show ProgressBar and disable user interaction
        progressBar.visibility = View.VISIBLE
        disableUserInteraction()

        val safepass = utilidade.hashPassword(newPassword)
        lifecycleScope.launch {
            try {
                val passwordUpdated = userEmail?.let { email ->
                    // Assuming dbManager.updatePassword(email, newPassword) returns Boolean
                    dbManager.updatePassword(email, safepass)
                } ?: false

                withContext(Dispatchers.Main) {
                    // Hide ProgressBar and re-enable user interaction
                    progressBar.visibility = View.GONE
                    enableUserInteraction()

                    if (passwordUpdated) {
                        Toast.makeText(this@NewPasswordActivity, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                        // Navigate to LoginActivity
                        val intent = Intent(this@NewPasswordActivity, MenuActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@NewPasswordActivity, "Failed to update password.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Hide ProgressBar and re-enable user interaction
                    progressBar.visibility = View.GONE
                    enableUserInteraction()

                    Toast.makeText(this@NewPasswordActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveLoginState(email: String) {
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", true)
            putString("userEmail", email)
            apply()
        }
    }

    private fun disableUserInteraction() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun enableUserInteraction() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}

