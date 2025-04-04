package com.example.ezinvoice.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ezinvoice.apis.addcatageoryapi
import com.example.ezinvoice.models.CatagoryModel
import com.example.ezinvoice.models.CatagoryResponce
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch

class AddCategoryViewmodel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val catagoryMLD = MutableLiveData<String>("")

    private val _categoryResponse = MutableLiveData<CatagoryResponce?>()
    val categoryResponse: LiveData<CatagoryResponce?> get() = _categoryResponse

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _issuccessfull = MutableLiveData(false)
    val issuccessfull: LiveData<Boolean> get() = _issuccessfull

    private val businessid: String? = sharedPreferences.getString("business-id", null)

    private val api = RetrofitClient.createService(addcatageoryapi::class.java)

    fun Onaddcategory() {
        val catName = catagoryMLD.value?.trim()

        // Check if businessid is null
        if (businessid.isNullOrEmpty()) {
            _errorMessage.value = "Business ID is missing! please wipe the app data"
            Log.e("AddCategoryViewmodel", "Business ID is null or empty")
            return
        }

        // Check if category name is empty
        if (catName.isNullOrEmpty()) {
            _errorMessage.value = "Category name cannot be empty!"
            Log.e("AddCategoryViewmodel", "Category name is null or empty")
            return
        }

        viewModelScope.launch {
            try {
                val categoryRequestModel = CatagoryModel(businessid, catName,"")
                val response = api.addcategory(categoryRequestModel)

                if (response.isSuccessful && response.body() != null) {
                    _categoryResponse.value = response.body()
                    _errorMessage.value = "Category added successfully"
                    _issuccessfull.value = true
                } else {
                    _errorMessage.value = "Failed to add category. Error: ${response.errorBody()?.string()}"
                    _issuccessfull.value = false
                    Log.e("AddCategoryViewmodel", "Error Response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
                _issuccessfull.value = false
                Log.e("AddCategoryViewmodel", "Exception: ${e.message}", e)
            }
        }
    }
}
