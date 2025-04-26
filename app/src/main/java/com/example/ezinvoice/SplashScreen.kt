package com.example.ezinvoice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(R.color.status_bar_color)
        } else {
            Toast.makeText(this, "Build Version is not compatible", Toast.LENGTH_SHORT).show()
        }

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)
        val businessToken = sharedPreferences.getString("business_token", null)

        // ✅ Improved logic to determine the next activity
        window.decorView.postDelayed({
            val nextActivity = when {
                authToken == null -> SignIn::class.java
                businessToken == "" -> Business_Info::class.java
                else -> MainActivity::class.java
            }
            startActivity(Intent(this, nextActivity))
            finish()
        }, 3000) // 3-second delay
    }
}
