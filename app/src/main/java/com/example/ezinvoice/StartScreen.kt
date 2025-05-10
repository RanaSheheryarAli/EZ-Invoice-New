package com.example.ezinvoice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.ActivityStartScreenBinding

class StartScreen : AppCompatActivity() {
    lateinit var databinding:ActivityStartScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       databinding=DataBindingUtil.setContentView(this,R.layout.activity_start_screen)

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        databinding.btnGetStarted.setOnClickListener {
            val intent= Intent(this@StartScreen,Signup::class.java)
            startActivity(intent)
        }
        databinding.tvSignIn.setOnClickListener{
            val intent=Intent(this@StartScreen,Signup::class.java)
            startActivity(intent)
        }
    }
    override fun onStart() {
        super.onStart()
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val authToken = prefs.getString("auth_id", null)
        val businessToken = prefs.getString("business_id", null)

        if (!authToken.isNullOrEmpty() && !businessToken.isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}