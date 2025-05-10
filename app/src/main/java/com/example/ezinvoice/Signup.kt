package com.example.ezinvoice

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ezinvoice.databinding.ActivitySignupBinding
import com.example.ezinvoice.viewmodels.SignupViewmodel

class Signup : AppCompatActivity() {

    private lateinit var signupviewmodel: SignupViewmodel
    private lateinit var databinding: ActivitySignupBinding
    private lateinit var prefs: SharedPreferences

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

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        signupviewmodel = ViewModelProvider(this)[SignupViewmodel::class.java]
        databinding.lifecycleOwner = this
        databinding.signupviewmodel = signupviewmodel

        // ✅ Observe success or failure
        signupviewmodel.issuccessfull.observe(this) { isSuccess ->
            if (isSuccess) {

                val userId = signupviewmodel.userid  // ✅ fetch only after success

                Log.d("Signup", "Saving userId: $userId")
                prefs.edit().putString("auth_id", userId).apply()

                Log.d("checkuserid", "checkuserid from pre: ${prefs.getString("auth_id", null)}}")
                val intent = Intent(this@Signup, Business_Info::class.java)

                startActivity(intent)
                finish()
            }
        }

        // ✅ Observe error messages separately to prevent multiple Toasts
        signupviewmodel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                signupviewmodel.clearError()  // Clear error after showing
            }
        }

        databinding.tvSignIn.setOnClickListener {
            val intent = Intent(this@Signup, SignIn::class.java)
            startActivity(intent)
        }

    }
}
