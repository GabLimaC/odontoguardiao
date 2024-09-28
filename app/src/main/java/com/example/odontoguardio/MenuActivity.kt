package com.example.odontoguardio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
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
            Toast.makeText(this, "botao locais", Toast.LENGTH_SHORT).show()
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

}
