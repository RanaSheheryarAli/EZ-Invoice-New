package com.example.ezinvoice

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.ActivitySignupBinding

class Signup : AppCompatActivity() {
    lateinit var databinding:ActivitySignupBinding
   lateinit var name:String
   lateinit var email :String
   lateinit var password:String
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
      databinding= DataBindingUtil.setContentView(this,R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        databinding.tvSignIn.setOnClickListener {
            val intent =Intent(this@Signup,SignIn::class.java)
            startActivity(intent)
        }

        databinding.btnsingup.setOnClickListener{
            check_is_not_empty()
            name=databinding.etName.text.toString()
            email=  databinding.etEmail.text.toString()
            password=  databinding.etPassword.text.toString()


        }

    }

    fun check_is_not_empty(){

        if(databinding.etName.text.isEmpty() && databinding.etEmail.text.isEmpty() && databinding.etPassword.text.isEmpty()){
            val intent =Intent(this@Signup,OTP::class.java)
            startActivity(intent)

        }
        else{
            Toast.makeText(this,"",Toast.LENGTH_SHORT).show()
        }
    }


}