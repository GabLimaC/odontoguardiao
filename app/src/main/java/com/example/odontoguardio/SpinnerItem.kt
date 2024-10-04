package com.example.odontoguardio

data class SpinnerItem(val name: String, val group: Int){
    override fun toString(): String {
        return name
    }
}