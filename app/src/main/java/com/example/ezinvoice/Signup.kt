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
import androidx.lifecycle.ViewModelProvider
import com.example.ezinvoice.databinding.ActivitySignupBinding
import com.example.ezinvoice.viewmodels.SignupViewmodel

class Signup : AppCompatActivity() {

    private lateinit var signupviewmodel: SignupViewmodel
    private lateinit var databinding: ActivitySignupBinding


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        signupviewmodel = ViewModelProvider(this)[SignupViewmodel::class.java]
        databinding.lifecycleOwner = this
        databinding.signupviewmodel = signupviewmodel




            // Observe success or failure
            signupviewmodel.issuccessfull.observe(this, { isSuccess ->
                if (isSuccess) {
                    val intent = Intent(this@Signup, SignIn::class.java)
                    startActivity(intent)
                    finish()
                }
                // Only show toast if a signup attempt was made
                else if (signupviewmodel.attemptedSignup) {
                    Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show()
                }
            })





        databinding.tvSignIn.setOnClickListener {
            val intent = Intent(this@Signup, SignIn::class.java)
            startActivity(intent)
        }
    }
}
