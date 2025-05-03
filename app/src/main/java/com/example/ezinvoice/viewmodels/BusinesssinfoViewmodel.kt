package com.example.ezinvoice.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.*
import com.example.ezinvoice.apis.BusinessinfoApi
import com.example.ezinvoice.apis.UploadImgApi
import com.example.ezinvoice.models.BusinessInfo
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class BusinesssinfoViewmodel(application: Application) : AndroidViewModel(application) {

    val businessname = MutableLiveData("")
    val logoUri = MutableLiveData<String>()
    val logoLocalUri = MutableLiveData<Uri?>()
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

    private val prefs = application.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val userid = prefs.getString("auth_id", null)


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _businessinfo = MutableLiveData<BusinessInfo?>()
    val businessinfo: LiveData<BusinessInfo?> = _businessinfo

    private val _issuccessfull = MutableLiveData(false)
    val issuccessfull: LiveData<Boolean> = _issuccessfull

    private val api = RetrofitClient.createService(BusinessinfoApi::class.java)
    private val uploadApi = RetrofitClient.createService(UploadImgApi::class.java)

    fun onupdateClick() {
        val fields = listOfNotNull(
            businessname.value, ownername.value, gstin.value, adress.value,
            email.value, phone.value, websitelink.value, currency.value,
            country.value, numberformate.value, dateformate.value, signature1.value
        )

        Log.e("BusinesssinfoViewmodel", "User ID from prefs: $userid")
        if (fields.any { it.trim().isEmpty() }) {
            _errorMessage.value = "All fields are required!"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value ?: "").matches()) {
            _errorMessage.value = "Invalid email format!"
            return
        }

        val business = userid?.let {
            BusinessInfo(
                _id = "",
                userId = it,
                name = businessname.value!!,
                ownername = ownername.value!!,
                gstin = gstin.value!!,
                address = adress.value!!,
                email = email.value!!,
                contact = phone.value!!,
                website = websitelink.value!!,
                country = country.value!!,
                currency = currency.value!!,
                numberformate = numberformate.value!!,
                dateformate = dateformate.value!!,
                signature = signature1.value!!,
                logoUrl = logoUri.value,
                categoryIds = emptyList(),
                createdAt = ""
            )
        } ?: run {
            _errorMessage.value = "User ID is missing"
            return
        }

        viewModelScope.launch {
            try {
                val response = api.createbusiness(business)
                if (response.isSuccessful) {


                    val data = response.body()

                    data?.let {
                        prefs.edit().apply {
                            putString("auth_id", it.userId)
                            putString("business_id", it._id)          // ✅ this should be used consistently
                            putString("business_name", it.name)
                            putString("business_owner", it.ownername)
                            putString("business_gstin", it.gstin)
                            putString("business_adress", it.address)
                            putString("business_email", it.email)
                            putString("business_phone", it.contact)
                            putString("business_website", it.website)
                            putString("business_country", it.country)
                            putString("business_currency", it.currency)
                            putString("business_numberformate", it.numberformate)
                            putString("business_dateformate", it.dateformate)
                            putString("business_signature", it.signature)
                            putString("business_logouri", it.logoUrl)
                        }.apply() // ✅ this line was missing — CRITICAL!
                    }

                    _businessinfo.value = data
                    _issuccessfull.value = true
                    _errorMessage.value = "Business created successfully!"


                } else {
                    _errorMessage.value = response.errorBody()?.string() ?: "Unexpected error"
                    _issuccessfull.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
                _issuccessfull.value = false
            }
        }
    }

    fun uploadLogoImage(context: Context) {
        val imageUri = logoLocalUri.value ?: return
        val file = getFileFromUri(context, imageUri) ?: run {
            _errorMessage.value = "File conversion failed"
            return
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("logo", file.name, requestFile)

        viewModelScope.launch {
            try {
                val response = uploadApi.uploadBusinessLogo(body)
                if (response.isSuccessful) {
                    logoUri.value = response.body()?.string()
                    _errorMessage.value = "Logo uploaded successfully"
                    onupdateClick() // only after success
                } else {
                    _errorMessage.value = "Upload failed: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Upload error: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: -1
        returnCursor?.moveToFirst()
        val name = if (nameIndex != -1) returnCursor?.getString(nameIndex) else "temp_file"
        returnCursor?.close()

        val file = File(context.cacheDir, name ?: "temp_file")
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
