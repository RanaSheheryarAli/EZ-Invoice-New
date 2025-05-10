package com.example.ezinvoice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        val prefs: SharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val authToken = prefs.getString("auth_id", null)
        val businessToken = prefs.getString("business_id", null)

        Log.d("SplashScreen", "Auth Token: $authToken, Business Token: $businessToken")

        // Decide the next screen
        window.decorView.postDelayed({
            val nextActivity = when {
                authToken.isNullOrEmpty() -> SignIn::class.java
                businessToken.isNullOrEmpty() -> Business_Info::class.java
                else -> MainActivity::class.java
            }
            startActivity(Intent(this, nextActivity))
            finish()
        }, 1000)
    }
}
