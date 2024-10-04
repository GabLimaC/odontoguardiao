package com.example.odontoguardio

import DatabaseManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MenuActivity : AppCompatActivity() {

    private lateinit var iv_greeting: TextView
    private lateinit var dbManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btn_id : Button = findViewById(R.id.btn_identificar)
        val btn_denuncia : Button = findViewById(R.id.btn_denuncia)
        val btn_deveres : Button = findViewById(R.id.btn_deveres)
        val btn_faq : Button = findViewById(R.id.btn_faq)
        val btn_locais : Button = findViewById(R.id.btn_locais)
        val btn_perfil : Button = findViewById(R.id.btn_perfil)
        val btn_sobre : Button = findViewById(R.id.btn_sobre)
        dbManager = DatabaseManager()
        iv_greeting = findViewById(R.id.text_greeting)

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", null)

        //para mostrar o nome
        if (userEmail != null){

            lifecycleScope.launch{
                val nome = getUserName(userEmail, dbManager)
                withContext(Dispatchers.Main){
                    iv_greeting.text = "Olá, $nome"
                }
            }

        }else{
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




        btn_id.setOnClickListener {
            val intent = Intent(this, IdentifyActivity::class.java)
            startActivity(intent)
        }
        btn_denuncia.setOnClickListener {
            val intent = Intent(this, DenunciaActivity::class.java)
            startActivity(intent)
        }
        btn_deveres.setOnClickListener {
            val intent = Intent(this, DeveresActivity::class.java)
            startActivity(intent)
        }
        btn_faq.setOnClickListener {
            val intent = Intent(this, FaqActivity::class.java)
            startActivity(intent)
        }
        btn_locais.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        btn_perfil.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        btn_sobre.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }



    }

    override fun onResume() {
        super.onResume()

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", null)

        if (userEmail != null){

            lifecycleScope.launch{
                val nome = getUserName(userEmail, dbManager)
                withContext(Dispatchers.Main){
                    iv_greeting.text = "Olá, $nome"
                }
            }

        }else{
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

    }

    private suspend fun getUserName(email: String?, dbManager: DatabaseManager): String? {
        if (email == null) return null

        return try {
            dbManager.readUserName(email)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
