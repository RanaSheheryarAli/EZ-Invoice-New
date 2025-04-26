package com.example.ezinvoice.viewmodels


import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ezinvoice.apis.addcatageoryapi
import com.example.ezinvoice.models.SubCatagoryModel
import com.example.ezinvoice.models.SubCategoryResponse
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch

class AddSubcategoryViewmodel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    val subcategoryMLD = MutableLiveData<String>("")
    val categoryId = MutableLiveData<String?>()

    private val _subCategoryResponse = MutableLiveData<SubCategoryResponse?>()
    val subCategoryResponse: LiveData<SubCategoryResponse?> get() = _subCategoryResponse

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isSuccessful = MutableLiveData(false)
    val isSuccessful: LiveData<Boolean> get() = _isSuccessful

    private val api = RetrofitClient.createService(addcatageoryapi::class.java)

    fun Onaddsubcategory() {
        val subcatName = subcategoryMLD.value?.trim()
        val catId = categoryId.value

        if (catId.isNullOrEmpty()) {
            _errorMessage.value = "Category ID is missing!"
            Log.e("AddSubcategoryViewmodel", "Category ID is missing")
            return
        }

        if (subcatName.isNullOrEmpty()) {
            _errorMessage.value = "SubCategory name cannot be empty!"
            Log.e("AddSubcategoryViewmodel", "Subcategory name is null or empty")
            return
        }

        viewModelScope.launch {
            try {
                val subCategoryRequest = SubCatagoryModel(
                    categoryId = catId,
                    name = subcatName,
                    products = emptyList()
                )
                val response = api.addSubCategory(subCategoryRequest)

                if (response.isSuccessful && response.body() != null) {
                    _subCategoryResponse.value = response.body()
                    _errorMessage.value = "SubCategory added successfully"
                    _isSuccessful.value = true
                } else {
                    _errorMessage.value = "Failed to add SubCategory. Error: ${response.errorBody()?.string()}"
                    _isSuccessful.value = false
                    Log.e("AddSubcategoryViewmodel", "Error Response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
                _isSuccessful.value = false
                Log.e("AddSubcategoryViewmodel", "Exception: ${e.message}", e)
            }
        }
    }
}
