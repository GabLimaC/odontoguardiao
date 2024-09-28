package com.example.odontoguardio

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var dbManager: DatabaseManager

    @Test
    fun setUp() {
        dbManager = DatabaseManager()
        // Initialize the database connection for testing
        dbManager.getConnection()

        val result = dbManager.insertUser("test@example.com", "Test", "user", "password123")
        assertNotNull(result)
    }



}