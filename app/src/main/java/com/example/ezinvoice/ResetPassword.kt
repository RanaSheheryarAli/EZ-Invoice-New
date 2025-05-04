package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.ActivityResetPasswordBinding
import com.example.ezinvoice.databinding.ActivitySignupBinding

class ResetPassword : AppCompatActivity() {

    lateinit var databinding: ActivityResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       databinding=DataBindingUtil.setContentView(this,R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = getColor(R.color.status_bar_color)


        databinding.btnResetPassword.setOnClickListener {
            val intent = Intent(this@ResetPassword,SignIn::class.java)
            startActivity(intent)
        }
    }
}