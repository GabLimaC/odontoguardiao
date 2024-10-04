package com.example.odontoguardio

import DatabaseManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {
    private val dbManager = DatabaseManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val utilidade = Utilidade()
        val btn_enter : Button = findViewById(R.id.btn_enter)
        val btn_new_passwd : Button = findViewById(R.id.btn_forgot_passwd)
        val btn_create_acc : Button = findViewById(R.id.btn_create_acc)
        val emailet: EditText = findViewById(R.id.et_email)
        val passwdet: EditText = findViewById(R.id.et_password)
        val progressBar: ProgressBar = findViewById(R.id.progress_bar)


        btn_enter.setOnClickListener {
            val email = emailet.text.toString()
            val pass = passwdet.text.toString()

            val (isValid, message) = utilidade.validateEmailAndPass(email, pass)
            if (isValid) {
                // Show ProgressBar and disable user interaction
                progressBar.visibility = View.VISIBLE
                disableUserInteraction()

                lifecycleScope.launch {
                    try {
                        val loginSuccess = dbManager.login(email, pass)
                        withContext(Dispatchers.Main) {
                            // Hide ProgressBar and re-enable user interaction
                            progressBar.visibility = View.GONE
                            enableUserInteraction()

                            if (loginSuccess) {

                                saveLoginState(email)

                                val intent = Intent(applicationContext, MenuActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(applicationContext, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            // Hide ProgressBar and re-enable user interaction
                            progressBar.visibility = View.GONE
                            enableUserInteraction()

                            Toast.makeText(applicationContext, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // Validation failed
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }

        btn_create_acc.setOnClickListener{
            val intent = Intent(applicationContext, SignupActivity::class.java)
            startActivity(intent)
        }

        btn_new_passwd.setOnClickListener{
            val intent = Intent(applicationContext, RecoverpassActivity::class.java)
            startActivity(intent)
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


