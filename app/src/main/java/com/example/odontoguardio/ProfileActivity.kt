package com.example.odontoguardio

import DatabaseManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {
    private lateinit var btnBack: Button
    private lateinit var btnDados: Button
    private lateinit var btnNovasenha: Button
    private lateinit var btnAvalie: Button
    private lateinit var btnSair: Button

    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        btnBack = findViewById(R.id.btn_back)
        btnDados = findViewById(R.id.btn_meusDados)
        btnNovasenha = findViewById(R.id.btn_alterarSenha)
        btnAvalie = findViewById(R.id.btn_avalie)
        btnSair = findViewById(R.id.btn_sair)

        userEmail = intent.getStringExtra("email")

        btnBack.setOnClickListener {
            finish()
        }

        btnDados.setOnClickListener {
            val intent = Intent(this@ProfileActivity, UserDataActivity::class.java)
            startActivity(intent)
        }


        btnNovasenha.setOnClickListener {
            val intent = Intent(this@ProfileActivity, NewPasswordActivity::class.java)
            startActivity(intent)
        }

        btnAvalie.setOnClickListener {
            val packageName = packageName
            try {
                // Attempt to open Play Store app
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            } catch (e: ActivityNotFoundException) {
                // Fallback to web browser
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }


// In your Activity's onCreate method
        btnSair.setOnClickListener {
            // Example: Clearing SharedPreferences (if used for session)
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


    }

    private fun getUserData(email: String?, dbManager: DatabaseManager): User?{
        if (email != null) {
            lifecycleScope.launch {
                try {
                    val name = dbManager.readUser(email)
                    withContext(Dispatchers.Main) {
                        return@withContext name
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}