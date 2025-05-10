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
import androidx.core.view.WindowInsetsControllerCompat
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

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

                        Log.d(TAG, "Sign-in successful, checking business status...")

                        val businessToken = user.businessID


                        sharedPreferences.edit().putString("auth_id", user.id).apply()
                        if (businessToken.isNullOrEmpty()) {
                            Log.d(TAG, "No business info found, redirecting to Business_Info")
                            val intent = Intent(this@SignIn, Business_Info::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.d(TAG, "Business info exists, fetching business data...")

                            // Fetch and cache business info
                            signinviewmodel.fetchBusinessInfo(
                                context = this@SignIn,
                                userId = user.id ?: "",
                                onSuccess = {
                                    startActivity(Intent(this@SignIn, MainActivity::class.java))
                                    finish()
                                },
                                onError = { error ->
                                    Toast.makeText(this@SignIn, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                    } catch (e: Exception) {
                        Log.e(TAG, "Navigation error: ${e.localizedMessage}")
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