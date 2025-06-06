package com.example.ezinvoice

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ezinvoice.databinding.ActivitySignInBinding
import com.example.ezinvoice.viewmodels.SigninViewmodel

class SignIn : AppCompatActivity() {

    lateinit var databinding: ActivitySignInBinding
    private lateinit var signinviewmodel: SigninViewmodel


    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val TAG = "sign.kt"

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
//        val businessToken = sharedPreferences.getString("business_token", null)

        signinviewmodel = ViewModelProvider(this)[SigninViewmodel::class.java]
        databinding.lifecycleOwner = this
        databinding.signinviewmodel = signinviewmodel



        // ✅ Observe success or failure
        signinviewmodel.issuccessfull.observe(this) { isSuccess ->
            if (isSuccess) {
                signinviewmodel.loginResponse.value?.user?.let { user ->
                    try {

                        Log.d(TAG, "Sign-in successful, navigating to Business_Info Activity")

//                        Log.e(TAG, "check business token: ${businessToken} ")

                        val business_tocken = signinviewmodel.loginResponse.value!!.user.businessID
                        if (business_tocken == "") {
                            Log.d("check business error", "Sign-in successful, navigating to Business_Info Activity")
                            val intent = Intent(this@SignIn, Business_Info::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.d("check Main error", "Sign-in successful, navigating to Main Activity")
                            val intent = Intent(this@SignIn, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    } catch (e: Exception) {
                        Log.e("check business error after catch", "Error while navigating to Business_Info: ${e.localizedMessage}")
                    }
                } ?: Log.e(TAG, "User data is null")
            }
        }


        // ✅ Observe error messages separately to prevent multiple Toasts
        signinviewmodel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

                signinviewmodel.clearError()  // Clear error after showing

            }
        }


        databinding.tvEmail.setOnClickListener {
            databinding.tvEmail.setTextColor(ContextCompat.getColor(this, R.color.buttoncolor))
            databinding.tvphonenumber.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.light_black
                )
            )
            databinding.phoneContainer.visibility = View.GONE
            databinding.tvphone.visibility = View.GONE
            databinding.etemail.visibility = View.VISIBLE
            databinding.tvemail.visibility = View.VISIBLE
        }

        databinding.tvphonenumber.setOnClickListener {
            databinding.tvphonenumber.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.buttoncolor
                )
            )
            databinding.tvEmail.setTextColor(ContextCompat.getColor(this, R.color.light_black))
            databinding.phoneContainer.visibility = View.VISIBLE
            databinding.tvphone.visibility = View.VISIBLE
            databinding.etemail.visibility = View.GONE
            databinding.tvemail.visibility = View.GONE
        }
        databinding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this@SignIn, ForgotPassword::class.java)
            startActivity(intent)
        }
        databinding.tvSignup.setOnClickListener {
            val intent = Intent(this@SignIn, Signup::class.java)
            startActivity(intent)
        }


    }
}