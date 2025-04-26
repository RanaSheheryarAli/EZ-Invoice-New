package com.example.ezinvoice.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ezinvoice.apis.AuthApi
import com.example.ezinvoice.models.LoginModel
import com.example.ezinvoice.models.LoginResponse
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch
import java.lang.Exception

class SigninViewmodel(application: Application) : AndroidViewModel(application) {

    val EmailLD = MutableLiveData("")
    val passwordLD = MutableLiveData("")
    var attemptedSignup = false
    private val _issuccessfull = MutableLiveData(false)
    val issuccessfull: LiveData<Boolean> get() = _issuccessfull
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> get() = _loginResponse

    private val sharedPreferences =
        application.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    private val api = RetrofitClient.createService(AuthApi::class.java)

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

            viewModelScope.launch {
                try {
                    val LoginrequestModel = LoginModel(email, password)
                    val response = api.signin1(LoginrequestModel)
                    response.body()?.success=="Signin successful"
                    if(response.body()?.success=="Signin successful"){

                        sharedPreferences.edit().putString("business-id", response.body()?.user?.businessID).apply()
                        sharedPreferences.edit().putString("auth_token", response.body()?.token).apply()
                        sharedPreferences.edit().putString("user_id", response.body()?.user?.id).apply()

                        _loginResponse.value = response.body()
                        _errorMessage.value = "Signin Successful"
                        _issuccessfull.value = true
                    }
                    else{
                        _errorMessage.value = "Signin UnSuccessful"
                        _issuccessfull.value = false
                    }

                } catch (e: Exception) {
                    Log.e("Signin", "Error: ${e.message}", e)
                    _errorMessage.value = "Network Error: ${e.message}"
                    _issuccessfull.value = false
                }
            }
        } else {
            _errorMessage.value = "All fields are required!"
            _issuccessfull.value = false
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return email.matches(emailPattern)
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
