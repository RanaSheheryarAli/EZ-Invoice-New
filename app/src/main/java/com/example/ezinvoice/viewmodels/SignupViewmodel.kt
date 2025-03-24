package com.example.ezinvoice.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ezinvoice.apis.AuthApi
import com.example.ezinvoice.models.AppUser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class SignupViewmodel : ViewModel() {

    // Two-way binding using MutableLiveData
    val UsernameLD = MutableLiveData<String>("")
    val EmailLD = MutableLiveData<String>("")
    val passwordLD = MutableLiveData<String>("")

    var attemptedSignup = false


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _issuccessfull = MutableLiveData(false)
    val issuccessfull: LiveData<Boolean> get() = _issuccessfull

    // Retrofit setup
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:5000/api/auth/")
//        .baseUrl("http://192.168.100.45:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Email validation regex
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        )
        return emailPattern.matcher(email).matches()
    }

    private val api = retrofit.create(AuthApi::class.java)

    // Signup button click handler
    fun onSignupClick() {
        val username = UsernameLD.value?.trim() ?: ""
        val email = EmailLD.value?.trim() ?: ""
        val password = passwordLD.value?.trim() ?: ""


        attemptedSignup = true
        Log.d("Signup", "Username: $username, Email: $email, Password: $password")

        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            // Validate email format
            if (!isValidEmail(email)) {
                _errorMessage.value = "Invalid email format!"
                Log.d("Signup", "Invalid Email: $email")
                _issuccessfull.value = false
                return
            }

            // Validate password length (minimum 6 characters)
            if (password.length < 6) {
                _errorMessage.value = "Password must be at least 6 characters!"
                Log.d("Signup", "Weak Password")
                _issuccessfull.value = false
                return
            }


            val request = AppUser("", username, email, password,"")

            // Convert request to JSON and log it
            val gson = com.google.gson.Gson()
            Log.d("Signup", "JSON Sent: ${gson.toJson(request)}")

            val call = api.signup1(request)
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        _errorMessage.value = "User Created"
                        Log.d("Signup", "Signup Successful")
                        _issuccessfull.value = true
                    } else {
                        _errorMessage.value = response.errorBody()?.string()
                        Log.d("Signup", "Signup Failed: ${response.errorBody()?.string()}")
                        _issuccessfull.value = false
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    _errorMessage.value = t.message
                    Log.d("Signup", "Network Error: ${t.message}")
                    _issuccessfull.value = false
                }
            })
        } else {
            _errorMessage.value = "All fields are Required"
            _issuccessfull.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

}
