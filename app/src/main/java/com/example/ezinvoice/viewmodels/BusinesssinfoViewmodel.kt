package com.example.ezinvoice.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ezinvoice.apis.BusinessinfoApi
import com.example.ezinvoice.models.BusinessInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class BusinesssinfoViewmodel : ViewModel() {

    // Two-way binding using MutableLiveData
    val businessname = MutableLiveData<String>("")
    val ownername = MutableLiveData<String>("")
    val gstin = MutableLiveData<String>("")
    val adress = MutableLiveData<String>("")
    val country = MutableLiveData<String>("")
    val email = MutableLiveData<String>("")
    val phone = MutableLiveData<String>("")
    val websitelink = MutableLiveData<String>("")
    val currency = MutableLiveData<String>("")
    val numberformate = MutableLiveData<String>("")
    val dateformate = MutableLiveData<String>("")
    val signature1 = MutableLiveData<String>("")

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _issuccessfull = MutableLiveData(false)
    val issuccessfull: LiveData<Boolean> get() = _issuccessfull

    // Retrofit setup
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:5000/api/businesses/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(BusinessinfoApi::class.java)

    // ✅ Corrected email validation
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        )
        return emailPattern.matcher(email).matches()
    }

    fun onupdateClick() {
        Log.d("onupdateClick", "businessname: ${businessname.value}")
        Log.d("onupdateClick", "ownername: ${ownername.value}")
        Log.d("onupdateClick", "gstin: ${gstin.value}")
        Log.d("onupdateClick", "adress: ${adress.value}")
        Log.d("onupdateClick", "email: ${email.value}")
        Log.d("onupdateClick", "phone: ${phone.value}")
        Log.d("onupdateClick", "websitelink: ${websitelink.value}")
        Log.d("onupdateClick", "currency: ${currency.value}")
        Log.d("onupdateClick", "numberformate: ${numberformate.value}")
        Log.d("onupdateClick", "dateformate: ${dateformate.value}")
        Log.d("onupdateClick", "signature1: ${signature1.value}")

        val name = businessname.value?.trim() ?: ""
        val owner = ownername.value?.trim() ?: ""
        val gstinstring = gstin.value?.trim() ?: ""
        val adress = adress.value?.trim() ?: ""
        val email = email.value?.trim() ?: ""
        val country=country.value?.trim() ?:""
        val contact = phone.value?.trim() ?: ""
        val website = websitelink.value?.trim() ?: ""
        val currency = currency.value?.trim() ?: ""
        val numberformate = numberformate.value?.trim() ?: ""
        val dateformate = dateformate.value?.trim() ?: ""
        val sig = signature1.value?.trim() ?: ""



        if (
            name.isEmpty() || owner.isEmpty() || country.isEmpty() ||
            gstinstring.isEmpty() || adress.isEmpty() || email.isEmpty() ||
            contact.isEmpty() || website.isEmpty() || currency.isEmpty() ||
            numberformate.isEmpty() || dateformate.isEmpty() || sig.isEmpty()

            ) {
            _errorMessage.value = "All fields are required! from viewmodel"
            _issuccessfull.value = false
            return
        }


        if (!isValidEmail(email)) {
            _errorMessage.value = "Invalid email format!"
            return
        }

        val request = BusinessInfo(
            "67ccb2c5eb806852ac7f10f6",
            name,
            owner,
            gstinstring,
            adress,
            email,
            contact,
            website,
            country,
            currency,
            numberformate,
            dateformate,
            sig,
            emptyList()
        )

        Log.d("BusinessInfo", "Sending JSON: ${com.google.gson.Gson().toJson(request)}")

        val call = api.createbusiness(request)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {
                    _errorMessage.value = "Business created successfully!"
                    _issuccessfull.value = true
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
