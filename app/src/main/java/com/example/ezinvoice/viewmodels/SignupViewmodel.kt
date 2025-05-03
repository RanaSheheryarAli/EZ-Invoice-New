package com.example.ezinvoice.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezinvoice.apis.AuthApi
import com.example.ezinvoice.models.AppUser
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class SignupViewmodel(application: Application) : AndroidViewModel(application) {

    // Two-way binding using MutableLiveData
    val UsernameLD = MutableLiveData<String>("")
    val EmailLD = MutableLiveData<String>("")
    val passwordLD = MutableLiveData<String>("")
    var attemptedSignup = false
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    private val _issuccessfull = MutableLiveData(false)
    val issuccessfull: LiveData<Boolean> get() = _issuccessfull

   var userid:String=""

    // Retrofit setup
    private val api = RetrofitClient.createService(AuthApi::class.java)

    // Email validation regex
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        )
        return emailPattern.matcher(email).matches()
    }


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
            val SignuprequestModel = AppUser("", username, email, password, "")
            viewModelScope.launch {
                try {
                    val response = api.signup1(SignuprequestModel)

                    if (response.isSuccessful) {


                        userid= response.body()?.newUser?._id.toString()
                        Log.d("checkuserid ", "from view model: ${userid}")
                        _issuccessfull.value = true
                        _errorMessage.value = "User Created"
                    } else {
                        _errorMessage.value = "Signup Failed: ${response.message()}"
                        Log.d("Signup", "Signup Failed: ${response.errorBody()?.string()}")
                        _issuccessfull.value = false
                    }
                } catch (e: Exception) {
                    _errorMessage.value = e.message ?: "An unknown error occurred"
                    Log.d("Signup", "Signup Failed: ${e.message}")
                    _issuccessfull.value = false
                }

            }

        } else {
            _errorMessage.value = "All fields are Required"
            _issuccessfull.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

}


