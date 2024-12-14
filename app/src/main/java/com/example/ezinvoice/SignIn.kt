package com.example.ezinvoice

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.ActivitySignInBinding

class SignIn : AppCompatActivity() {
    lateinit var databinding:ActivitySignInBinding
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
   databinding=DataBindingUtil.setContentView(this,R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
databinding.btnGetStarted.setOnClickListener{
            val intent=Intent(this@SignIn,OTP::class.java)
          startActivity(intent)
        }
        databinding.tvEmail.setOnClickListener{
            databinding.tvEmail.setTextColor(ContextCompat.getColor(this, R.color.buttoncolor))
            databinding.tvphonenumber.setTextColor(ContextCompat.getColor(this, R.color.light_black))
            databinding.phoneContainer.visibility= View.GONE
            databinding.tvphone.visibility=View.GONE
            databinding.etemail.visibility=View.VISIBLE
            databinding.tvemail.visibility=View.VISIBLE
        }

        databinding.tvphonenumber.setOnClickListener{
            databinding.tvphonenumber.setTextColor(ContextCompat.getColor(this, R.color.buttoncolor))
            databinding.tvEmail.setTextColor(ContextCompat.getColor(this, R.color.light_black))
            databinding.phoneContainer.visibility= View.VISIBLE
            databinding.tvphone.visibility=View.VISIBLE
            databinding.etemail.visibility=View.GONE
            databinding.tvemail.visibility=View.GONE
        }
        databinding.tvForgotPassword.setOnClickListener{
            val intent=Intent(this@SignIn,ForgotPassword::class.java)
            startActivity(intent)
        }
        databinding.tvSignup.setOnClickListener{
            val intent=Intent(this@SignIn,Signup::class.java)
            startActivity(intent)
        }



    }
}