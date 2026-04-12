package com.example.ezinvoice

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.ActivityOtpBinding

class OTP : AppCompatActivity() {
    lateinit var databinding:ActivityOtpBinding
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
      databinding=DataBindingUtil.setContentView(this,R.layout.activity_otp)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false


        databinding.btnVerifyotp.setOnClickListener{
            val intent= Intent(this@OTP,Business_Info::class.java)
            startActivity(intent)
        }
    }
}