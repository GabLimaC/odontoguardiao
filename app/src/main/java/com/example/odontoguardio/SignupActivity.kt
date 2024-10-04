package com.example.odontoguardio

import DatabaseManager
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

class SignupActivity : AppCompatActivity() {
    private lateinit var etNome: EditText
    private lateinit var etSobrenome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var etRepitaSenha: EditText
    private lateinit var btnCriarNovaConta: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var utilidade: Utilidade
    private lateinit var dbManager: DatabaseManager
    private lateinit var btnBack: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etNome = findViewById(R.id.et_nome)
        etSobrenome = findViewById(R.id.et_sobrenome)
        etEmail = findViewById(R.id.et_email)
        etSenha = findViewById(R.id.et_senha)
        etRepitaSenha = findViewById(R.id.et_repitaSenha)
        btnCriarNovaConta = findViewById(R.id.btn_criarNovaConta)
        progressBar = findViewById(R.id.progress_bar)
        btnBack = findViewById(R.id.btnBack)

        utilidade = Utilidade()
        dbManager = DatabaseManager()

        btnCriarNovaConta.setOnClickListener {
            createNewUser()
        }

        btnBack.setOnClickListener{
            finish()
        }

    }

    private fun createNewUser() {
        val nome = etNome.text.toString().trim()
        val sobrenome = etSobrenome.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val senha = etSenha.text.toString()
        val repitaSenha = etRepitaSenha.text.toString()

        val (isValid, message) = utilidade.validateSignup(nome, sobrenome, email, senha, repitaSenha)
        if (isValid) {
            // Show ProgressBar and disable user interaction
            progressBar.visibility = View.VISIBLE
            disableUserInteraction()
            val safepass = utilidade.hashPassword(senha)

            lifecycleScope.launch {
                try {
                    val userCreationSuccess = dbManager.createUser(nome, sobrenome, email, safepass)
                    withContext(Dispatchers.Main) {
                        // Hide ProgressBar and re-enable user interaction
                        progressBar.visibility = View.GONE
                        enableUserInteraction()

                        if (userCreationSuccess) {
                            // User created successfully
                            Toast.makeText(applicationContext, "Account created successfully!", Toast.LENGTH_SHORT).show()
                            // Navigate to the next screen if needed
                            val intent = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // User creation failed (e.g., email already exists)
                            Toast.makeText(applicationContext, "Email already in use.", Toast.LENGTH_SHORT).show()
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

    private fun disableUserInteraction() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun enableUserInteraction() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}