package com.example.odontoguardio

import DatabaseManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserDataActivity : AppCompatActivity() {
    private lateinit var etNome: EditText
    private lateinit var etSobrenome: EditText
    private lateinit var btnAlterar: Button
    private lateinit var dbManager: DatabaseManager
    private lateinit var utilidade: Utilidade
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBack: Button
    private lateinit var tvEmail: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        // Use lateinit or nullable types for view properties
        etNome = findViewById(R.id.etNome)
        etSobrenome = findViewById(R.id.etSobrenome)
        tvEmail = findViewById(R.id.tvEmail)
        btnAlterar = findViewById(R.id.btnSalvarAlteracoes)
        progressBar = findViewById(R.id.progress_bar_data)
        btnBack = findViewById(R.id.btnBack)

        val dbManager = DatabaseManager()
        val utilidade = Utilidade()

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", null)

        lifecycleScope.launch {
            showProgressBar()
            val usr = getUserData(userEmail, dbManager)
            hideProgressBar()

            withContext(Dispatchers.Main) {
                if (usr != null) {
                    etNome.setText(usr.nome)
                    etSobrenome.setText(usr.sobrenome)
                    tvEmail.text = userEmail

                } else {
                    Toast.makeText(this@UserDataActivity, "Algo deu errado, por favor faça login novamente", Toast.LENGTH_LONG).show()

                    with(sharedPref.edit()) {
                        clear()
                        apply()
                    }

                    // Navigate to LoginActivity
                    val intent = Intent(this@UserDataActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        btnAlterar.setOnClickListener {
            submitNewData()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun submitNewData() {
        val newNome = etNome.text.toString().trim()
        val newSobrenome = etSobrenome.text.toString().trim()
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", null)

        if (newNome.isEmpty() || newSobrenome.isEmpty() || userEmail.isNullOrEmpty()) {
            showToast("Please fill in all fields.")
            return
        }

        val (isValid, message) = utilidade.validateNewData(userEmail, newNome, newSobrenome)

        if (!isValid) {
            showToast(message)
            return
        }

        lifecycleScope.launch {
            try {
                showProgressBar()
                val (userUpdateSuccess, errorMessage) = dbManager.updateUser(userEmail, newNome, newSobrenome)
                hideProgressBar()

                if (userUpdateSuccess) {
                    showToast("Account updated successfully!")
                    navigateToProfileActivity()
                } else {
                    showToast("Could not update: $errorMessage")
                    Log.e("SubmitNewData", "Update failed: $errorMessage")
                }
            } catch (e: Exception) {
                hideProgressBar()
                showToast("An error occurred: ${e.message}")
                Log.e("SubmitNewData", "Exception during update", e)
            }
        }
    }


    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        disableUserInteraction()
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        enableUserInteraction()
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }


    private fun navigateToProfileActivity() {
        val intent = Intent(applicationContext, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    private suspend fun getUserData(email: String?, dbManager: DatabaseManager): User? {
        if (email == null) return null

        return try {
            dbManager.readUser(email)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    private fun disableUserInteraction() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun enableUserInteraction() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}