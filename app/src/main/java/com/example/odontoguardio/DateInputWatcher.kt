package com.example.odontoguardio

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.Locale

class DateInputWatcher(private val editText: EditText) : TextWatcher {
    private var isFormatting = false
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (isFormatting) return
        isFormatting = true

        val input = s.toString().replace("[^\\d]".toRegex(), "")
        val formatted = StringBuilder()

        for (i in input.indices) {
            if (i == 2 || i == 4) formatted.append("/")
            formatted.append(input[i])
            if (formatted.length == 10) break  // Stop after DD/MM/YYYY
        }

        editText.setText(formatted)
        editText.setSelection(formatted.length)

        isFormatting = false
    }
}