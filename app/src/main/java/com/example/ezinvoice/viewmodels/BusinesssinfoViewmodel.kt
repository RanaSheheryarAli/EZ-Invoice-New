package com.example.ezinvoice.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ezinvoice.apis.BusinessinfoApi
import com.example.ezinvoice.models.BusinessInfo
import com.example.ezinvoice.models.BusinessResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class BusinesssinfoViewmodel(application: Application) : AndroidViewModel(application) {

    val businessname = MutableLiveData("")
    val ownername = MutableLiveData("")
    val gstin = MutableLiveData("")
    val adress = MutableLiveData("")
    val country = MutableLiveData("")
    val email = MutableLiveData("")
    val phone = MutableLiveData("")
    val websitelink = MutableLiveData("")
    val currency = MutableLiveData("")
    val numberformate = MutableLiveData("")
    val dateformate = MutableLiveData("")
    val signature1 = MutableLiveData("")


    val sharedPreferences =
        getApplication<Application>().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val userid = sharedPreferences.getString("user_id", null)


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _businessinfo = MutableLiveData<BusinessInfo?>()
    val businessinfo: LiveData<BusinessInfo?> get() = _businessinfo

    private val _issuccessfull = MutableLiveData(false)
    val issuccessfull: LiveData<Boolean> get() = _issuccessfull

    // ✅ Fix: Use 10.0.2.2 for emulator
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:5000/api/businesses/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(BusinessinfoApi::class.java)

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        )
        return emailPattern.matcher(email).matches()
    }

    fun onupdateClick() {
        val name = businessname.value?.trim() ?: ""
        val owner = ownername.value?.trim() ?: ""
        val gstinstring = gstin.value?.trim() ?: ""
        val adress = adress.value?.trim() ?: ""
        val email = email.value?.trim() ?: ""
        val country = country.value?.trim() ?: ""
        val contact = phone.value?.trim() ?: ""
        val website = websitelink.value?.trim() ?: ""
        val currency = currency.value?.trim() ?: ""
        val numberformate = numberformate.value?.trim() ?: ""
        val dateformate = dateformate.value?.trim() ?: ""
        val sig = signature1.value?.trim() ?: ""

        if (listOf(
                name,
                owner,
                gstinstring,
                adress,
                email,
                country,
                contact,
                website,
                currency,
                numberformate,
                dateformate,
                sig
            ).any { it.isEmpty() }
        ) {
            _errorMessage.value = "All fields are required!"
            _issuccessfull.value = false
            return
        }

        if (!isValidEmail(email)) {
            _errorMessage.value = "Invalid email format!"
            return
        }

        val request = userid?.let {
            BusinessInfo(
                "", it, name, owner, gstinstring, adress, email, contact, website, country,
                currency, numberformate, dateformate, sig, emptyList(), ""
            )
        }

        val call = request?.let { api.createbusiness(it) }
        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {
                    response.body()?.string()?.let {
                        Log.e("RAW_RESPONSE", "onResponse: $it")

                        val gson = com.google.gson.Gson()
                        val businessResponse = gson.fromJson(it, BusinessResponse::class.java)

                        businessResponse?.business?.let { business ->
                            Log.e("Business Name", "onResponse: ${business.name}")
                            _businessinfo.value = business


                            businessResponse?.business?.let {
                                sharedPreferences.edit().putString("business_token", it._id).apply()
                                sharedPreferences.edit().putString("business-id", it._id).apply()
                                sharedPreferences.edit().putString("business_name", it.name).apply()
                                sharedPreferences.edit().putString("business_email", it.email).apply()

                                Log.e("SharedPreferences", "Business token saved: ${it._id}")
                            }


                            _errorMessage.value = "Business created successfully!"
                            _issuccessfull.value = true
                        }
                    }
                } else {
                    _errorMessage.value = response.errorBody()?.string()
                    _issuccessfull.value = false
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                _errorMessage.value = "Network error: ${t.message}"
                _issuccessfull.value = false
            }
        })
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
