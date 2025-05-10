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

    private val allInvoices = mutableListOf<InvoiceDisplay>()
    private val _visibleInvoices = MutableLiveData<List<InvoiceDisplay>>()
    val visibleInvoices: LiveData<List<InvoiceDisplay>> = _visibleInvoices

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentPage = 0
    private val pageSize = 5

    fun fetchInvoices(businessId: String) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.createService(InvoiceApi::class.java)
                val response = api.getInvoicesByBusinessId(businessId)
                if (response.isSuccessful) {
                    allInvoices.clear()
                    allInvoices.addAll(response.body() ?: emptyList())
                    currentPage = 1
                    _visibleInvoices.value = allInvoices.take(currentPage * pageSize)
                } else {
                    _error.value = "Failed to fetch invoices: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun loadMoreInvoices() {
        if ((currentPage * pageSize) < allInvoices.size) {
            currentPage++
            _visibleInvoices.value = allInvoices.take(currentPage * pageSize)
        }
    }

    fun hasMoreInvoices(): Boolean {
        return currentPage * pageSize < allInvoices.size
    }

    fun clearError() {
        _error.value = null
    }
}
