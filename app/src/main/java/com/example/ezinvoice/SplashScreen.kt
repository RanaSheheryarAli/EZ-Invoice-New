package com.example.ezinvoice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        Handler(Looper.getMainLooper()).postDelayed({
            if (authToken != null) {
                // ✅ If token exists, go to Business_Info screen
                startActivity(Intent(this, Business_Info::class.java))
            } else {
                // ✅ If no token, go to SignIn screen
                startActivity(Intent(this, SignIn::class.java))
            }
            finish()
        }, 3000) // 3-second delay
    }
}