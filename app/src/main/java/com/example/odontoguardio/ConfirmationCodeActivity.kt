package com.example.odontoguardio

import DatabaseManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConfirmationCodeActivity : AppCompatActivity() {

    private lateinit var etCode1: EditText
    private lateinit var etCode2: EditText
    private lateinit var etCode3: EditText
    private lateinit var etCode4: EditText
    private lateinit var etCode5: EditText
    private lateinit var etCode6: EditText
    private lateinit var btnProximo: Button
    private lateinit var btnBack: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var dbManager: DatabaseManager

    private lateinit var btnResend: Button
    private lateinit var tvCountdown: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val initialCountDown: Long = 60000 // 60 seconds in milliseconds
    private val countDownInterval: Long = 1000 // 1 second

    // Assuming you pass the email via Intent from RecoverPasswordActivity
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation_code)

        // Initialize UI elements
        etCode1 = findViewById(R.id.etCode1)
        etCode2 = findViewById(R.id.etCode2)
        etCode3 = findViewById(R.id.etCode3)
        etCode4 = findViewById(R.id.etCode4)
        etCode5 = findViewById(R.id.etCode5)
        etCode6 = findViewById(R.id.etCode6)
        btnProximo = findViewById(R.id.btnProximo)
        btnResend = findViewById(R.id.btnResend)
        btnBack = findViewById(R.id.btnBack)
        tvCountdown = findViewById(R.id.tvCountdown)
        progressBar = findViewById(R.id.progress_bar)
        dbManager = DatabaseManager()


        // Retrieve the email from Intent
        userEmail = intent.getStringExtra("email")

        // Setup TextWatchers for automatic focus
        setupTextWatchers()

        // Setup OnClickListener for submit button
        btnProximo.setOnClickListener {
            confirmCode()
        }

        btnBack.setOnClickListener{
            finish()
        }


        // Setup OnClickListener for resend button
        btnResend.setOnClickListener {
            resendCode()
            startCountDownTimer()
        }

        btnResend.isEnabled = true
    }

    //functions

    private fun setupTextWatchers() {
        etCode1.addTextChangedListener(GenericTextWatcher(etCode1, etCode2))
        etCode2.addTextChangedListener(GenericTextWatcher(etCode2, etCode3))
        etCode3.addTextChangedListener(GenericTextWatcher(etCode3, etCode4))
        etCode4.addTextChangedListener(GenericTextWatcher(etCode4, etCode5))
        etCode5.addTextChangedListener(GenericTextWatcher(etCode5, etCode6))
        etCode6.addTextChangedListener(GenericTextWatcher(etCode6, null)) // Last EditText
    }

    // Generic TextWatcher to handle focus shifting
    inner class GenericTextWatcher(private val currentEditText: EditText, private val nextEditText: EditText?) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            if (text.length == 1) {
                nextEditText?.requestFocus()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /* No-op */ }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { /* No-op */ }
    }

    // Handle backspace key to move focus to previous EditText
    private fun setupKeyListeners() {
        etCode2.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && etCode2.text.isEmpty()) {
                etCode1.requestFocus()
                true
            } else {
                false
            }
        }

        etCode3.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && etCode3.text.isEmpty()) {
                etCode2.requestFocus()
                true
            } else {
                false
            }
        }

        etCode4.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && etCode4.text.isEmpty()) {
                etCode3.requestFocus()
                true
            } else {
                false
            }
        }

        etCode5.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && etCode5.text.isEmpty()) {
                etCode4.requestFocus()
                true
            } else {
                false
            }
        }

        etCode6.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && etCode6.text.isEmpty()) {
                etCode5.requestFocus()
                true
            } else {
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupKeyListeners()
    }

    private fun confirmCode() {
        val code = getCodeFromEditTexts()

        if (code.length != 6) {
            Toast.makeText(this, "Please enter all 6 characters of the code.", Toast.LENGTH_SHORT).show()
            return
        }

        // Show ProgressBar and disable user interaction
        progressBar.visibility = View.VISIBLE
        disableUserInteraction()

        lifecycleScope.launch {
            try {
                val isValid = userEmail?.let { email ->
                    // Assuming dbManager.validateRecoveryCode(email, code) returns Boolean
                    dbManager.submitRecoveryCode(email, code)
                } ?: false

                withContext(Dispatchers.Main) {
                    // Hide ProgressBar and re-enable user interaction
                    progressBar.visibility = View.GONE
                    enableUserInteraction()

                    if (isValid) {
                        if(saveLoginState(userEmail)) {
                            Toast.makeText(
                                this@ConfirmationCodeActivity,
                                "Code confirmed!",
                                Toast.LENGTH_SHORT
                            ).show()
                            //Navigate to NewpasswordActivity
                            val intent = Intent(
                                this@ConfirmationCodeActivity,
                                NewPasswordActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(
                                this@ConfirmationCodeActivity,
                                "Something went wrong! Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this@ConfirmationCodeActivity, "Invalid or expired code.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Hide ProgressBar and re-enable user interaction
                    progressBar.visibility = View.GONE
                    enableUserInteraction()

                    Toast.makeText(this@ConfirmationCodeActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun resendCode() {
        userEmail?.let { email ->
            // Show ProgressBar and disable user interaction
            progressBar.visibility = View.VISIBLE
            disableUserInteraction()

            lifecycleScope.launch {
                try {
                    // Generate a new recovery code
                    val newRecoveryCode = generateRecoveryCode()

                    // Store the new recovery code
                    val isStored = dbManager.storeRecoveryCode(email, newRecoveryCode)

                    // Send the new recovery code via email
                    val emailSent = if (isStored) {
                        dbManager.sendRecoveryEmail(email, newRecoveryCode)
                    } else {
                        false
                    }

                    withContext(Dispatchers.Main) {
                        // Hide ProgressBar and re-enable user interaction
                        progressBar.visibility = View.GONE
                        enableUserInteraction()

                        if (isStored && emailSent) {
                            Toast.makeText(this@ConfirmationCodeActivity, "Recovery code resent to your email.", Toast.LENGTH_SHORT).show()
                            // Optionally, clear the EditTexts
                            clearEditTexts()
                        } else {
                            Toast.makeText(this@ConfirmationCodeActivity, "Failed to resend recovery code.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        // Hide ProgressBar and re-enable user interaction
                        progressBar.visibility = View.GONE
                        enableUserInteraction()

                        Toast.makeText(this@ConfirmationCodeActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } ?: run {
            Toast.makeText(this, "Email not found.", Toast.LENGTH_SHORT).show()
        }
        startCountDownTimer()
    }

    private fun saveLoginState(email: String?): Boolean{
        if(email != null) {
            val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("isLoggedIn", true)
                putString("userEmail", email)
                apply()
            }
            return true
        }
        return false
    }

    private fun getCodeFromEditTexts(): String {
        return listOf(etCode1, etCode2, etCode3, etCode4, etCode5, etCode6)
            .joinToString("") { it.text.toString().trim() }
            .uppercase() // Optional: convert to uppercase if codes are case-insensitive
    }

    private fun clearEditTexts() {
        listOf(etCode1, etCode2, etCode3, etCode4, etCode5, etCode6).forEach { it.text.clear() }
        etCode1.requestFocus()
    }

    private fun generateRecoveryCode(): String {
        val charset = ('A'..'Z') + ('0'..'9')
        return List(6) { charset.random() }.joinToString("")
    }

    private fun disableUserInteraction() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun enableUserInteraction() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun startCountDownTimer() {
        btnResend.isEnabled = false
        tvCountdown.visibility = View.VISIBLE
        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                tvCountdown.text = "($secondsRemaining)"
            }

            override fun onFinish() {
                btnResend.isEnabled = true
                tvCountdown.visibility = View.GONE
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }
}