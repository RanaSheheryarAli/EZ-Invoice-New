@file:Suppress("DEPRECATION")
package com.example.ezinvoice.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ezinvoice.apis.productapi
import com.example.ezinvoice.models.ProductResponse
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class ItemsFragmentViewmodel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    private val _productList = MutableLiveData<List<ProductResponse>>()
    val productList: LiveData<List<ProductResponse>> get() = _productList

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _issuccessfull = MutableLiveData<Boolean?>()
    val issuccessfull: LiveData<Boolean?> get() = _issuccessfull

    private val api = RetrofitClient.createService(productapi::class.java)

    fun fetchProducts() {
        val businessId = sharedPreferences.getString("business_id", "") ?: ""

        if (businessId.isEmpty()) {
            _errorMessage.value = "Business ID missing!"
            return
        }

        viewModelScope.launch {
            try {
                val response = api.getProductsByBusiness(businessId)
                if (response.isSuccessful) {
                    _productList.value = response.body()
                    _issuccessfull.value = true
                } else {
                    _errorMessage.value = "Nothing found in Products"
                    _issuccessfull.value = false
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("FetchProductsError", e.message.toString())
                _errorMessage.value = "API Error: ${e.message}"
            }
        }
    }

    fun searchProducts(query: String) {
        val businessId = sharedPreferences.getString("business_id", "") ?: ""
        if (businessId.isEmpty() || query.isEmpty()) return

        viewModelScope.launch {
            try {
                val response = api.searchProducts(businessId, query)
                if (response.isSuccessful) {
                    _productList.value = response.body()
                } else {
                    _productList.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("SearchProductsError", e.message.toString())
                _errorMessage.value = "Search Error: ${e.message}"
            }
        }
    }

    fun filterProductsByCategory(categoryId: String?, subcategoryId: String?) {
        val businessId = sharedPreferences.getString("business_id", "") ?: return

        viewModelScope.launch {
            try {
                val response = api.getProductsByBusiness(businessId)
                if (response.isSuccessful) {
                    val allProducts = response.body() ?: emptyList()
                    val filtered = allProducts.filter {
                        (categoryId == null || it.categoryId._id == categoryId) &&
                                (subcategoryId == null || it.subcategoryId._id == subcategoryId)
                    }
                    _productList.value = filtered
                }
            } catch (e: Exception) {
                Log.e("FilterProductsError", e.message.toString())
                _errorMessage.value = "Filter Error: ${e.message}"
            }
        }
    }
}
