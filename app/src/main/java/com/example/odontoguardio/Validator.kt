package com.example.odontoguardio
import android.util.Patterns

class Validator {
    fun validateEmailAndPass(email: String, pass: String): Pair<Boolean, String> {
        // Validate name
        if (pass.isEmpty()){
            return Pair(false, "senha invalida")
        }

        // Validate email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Pair(false, "Invalid email address.")
        }

        // If both name and email are valid
        return Pair(true, "Name and email are valid.")
    }

    fun validateSignup(nome: String, sobrenome: String, email: String, senha: String, repitaSenha: String): Pair<Boolean, String> {
        if (nome.isEmpty() || sobrenome.isEmpty() || email.isEmpty() || senha.isEmpty() || repitaSenha.isEmpty()) {
            return Pair(false, "Please fill in all fields.")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Pair(false, "Invalid email format.")
        }
        if (senha.length < 6) {
            return Pair(false, "Password must be at least 6 characters long.")
        }
        if (senha != repitaSenha) {
            return Pair(false, "Passwords do not match.")
        }
        // Add more password strength checks as needed
        return Pair(true, "")
    }


}