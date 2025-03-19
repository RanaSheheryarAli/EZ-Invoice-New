package com.example.ezinvoice.viewmodels


import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import android.util.Log
import android.content.SharedPreferences
import com.example.ezinvoice.apis.AuthApi
import com.example.ezinvoice.models.LoginModel
import com.example.ezinvoice.models.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class SigninViewmodel(application: Application) : AndroidViewModel(application) {


    val EmailLD = MutableLiveData<String>("")
    val passwordLD = MutableLiveData<String>("")
    var attemptedSignup = false
    private val _issuccessfull = MutableLiveData(false)
    val issuccessfull: LiveData<Boolean> get() = _issuccessfull
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage





    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    // Retrofit setup
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:5000/api/auth/")// ✅ Emulator use 10.0.2.2
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(AuthApi::class.java)

    // Email validation regex
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        )
        return emailPattern.matcher(email).matches()
    }

    fun onSigninClick() {
        val email = EmailLD.value?.trim() ?: ""
        val password = passwordLD.value?.trim() ?: ""

        attemptedSignup = true
        Log.d("Signin", "Email: $email, Password: $password")

        if (email.isNotEmpty() && password.isNotEmpty()) {
            if (!isValidEmail(email)) {
                _errorMessage.value = "Invalid email format!"
                _issuccessfull.value = false
                return
            }
            if (password.length < 6) {
                _errorMessage.value = "Password must be at least 6 characters!"
                _issuccessfull.value = false
                return
            }

            val request = LoginModel(email, password)
            val call = api.signin(request)

            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if (response.isSuccessful) {
                        val jsonResponse = response.body()?.string()
                        jsonResponse?.let {
                            Log.d("Signin", "Response: $it")

                            val gson = com.google.gson.Gson()
                            val loginResponse = gson.fromJson(it, LoginResponse::class.java)


                            if (loginResponse.success == "Signin successful") {
                                // ✅ Save Token to SharedPreferences


                                sharedPreferences.edit().putString("auth_token", loginResponse.token).apply()

                                Log.d("Signin", "Signin Successful")
                                _errorMessage.value = "Signin Successful"
                                _issuccessfull.value = true
                            } else {
                                _errorMessage.value = "Signin failed!"
                                _issuccessfull.value = false
                            }
                        }
                    } else {
                        _errorMessage.value = "Signin Failed"
                        _issuccessfull.value = false
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    _errorMessage.value = "Network Error: ${t.message}"
                    _issuccessfull.value = false
                }
            })
        } else {
            _errorMessage.value = "All fields are required!"
            _issuccessfull.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}




