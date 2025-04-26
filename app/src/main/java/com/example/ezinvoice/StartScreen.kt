package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.ActivityStartScreenBinding

class StartScreen : AppCompatActivity() {
    lateinit var databinding:ActivityStartScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       databinding=DataBindingUtil.setContentView(this,R.layout.activity_start_screen)
        databinding.btnGetStarted.setOnClickListener {
            val intent= Intent(this@StartScreen,Signup::class.java)
            startActivity(intent)
        }
        databinding.tvSignIn.setOnClickListener{
            val intent=Intent(this@StartScreen,Signup::class.java)
            startActivity(intent)
        }
// mohsin
        Log.e("TAG", "onCreate: testing.......", )
    }
}