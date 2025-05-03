package com.example.ezinvoice.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezinvoice.apis.InvoiceApi
import com.example.ezinvoice.models.InvoiceDisplay
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch

class HomeFragmentViewmodel : ViewModel() {

    private val _invoices = MutableLiveData<List<InvoiceDisplay>>()
    val invoices: LiveData<List<InvoiceDisplay>> = _invoices

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchInvoices(businessId: String) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.createService(InvoiceApi::class.java)
                val response = api.getInvoicesByBusinessId(businessId)
                if (response.isSuccessful) {
                    _invoices.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to fetch invoices: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
