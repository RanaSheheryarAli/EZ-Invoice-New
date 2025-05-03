package com.example.ezinvoice.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ezinvoice.apis.productapi
import com.example.ezinvoice.models.ProductModel
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch

class AddProductsViewmodel(application: Application) : AndroidViewModel(application) {

    val nameMLD = MutableLiveData<String>("")
    val barcodeMLD = MutableLiveData<String>("")
    val catagoryMLD = MutableLiveData<String>("")
    val subcatagoryMLD = MutableLiveData<String>("")
    val salepriceMLD = MutableLiveData<String>("")
    val dis_sale_priceMLD = MutableLiveData<String>("")
    val purchaseMLD = MutableLiveData<String>("")
    val texesMLD = MutableLiveData<String>("")
    val openingstockMLD = MutableLiveData<String>("")
    val dateMLD = MutableLiveData<String>("")
    val unit_priceMLD = MutableLiveData<String>("") // not used now
    val min_stockMLD = MutableLiveData<String>("")
    val locationMLD = MutableLiveData<String>("")

    val errorMessage = MutableLiveData<String>()
    val isSuccessful = MutableLiveData<Boolean>()

    private val api = RetrofitClient.createService(productapi::class.java)



    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)




    var selectedCategoryId: String? = null
    var selectedSubCategoryId: String? = null

    fun onSaveProduct(context: Context) {
        // ✅ Validation before sending API call
        if (selectedCategoryId.isNullOrEmpty() || selectedSubCategoryId.isNullOrEmpty()) {
            errorMessage.value = "Please select Category and Subcategory"
            Toast.makeText(context, "Please select Category and Subcategory", Toast.LENGTH_SHORT).show()
            return
        }

        val businessId = getBusinessId()

        if(businessId.isEmpty()) {
            errorMessage.value = "Business ID missing. Please login again!"
            Toast.makeText(context, "Business ID missing. Please login again!", Toast.LENGTH_SHORT).show()
            return
        }
        if(businessId.isNullOrEmpty()){
            errorMessage.value = "Business ID missing. Please login again!"
            Toast.makeText(context, "Business ID missing. Please login again!", Toast.LENGTH_SHORT).show()
            return
        }

        if (nameMLD.value.isNullOrBlank() ||
            barcodeMLD.value.isNullOrBlank() ||
            salepriceMLD.value.isNullOrBlank() ||
            dis_sale_priceMLD.value.isNullOrBlank() ||
            purchaseMLD.value.isNullOrBlank() ||
            texesMLD.value.isNullOrBlank() ||
            openingstockMLD.value.isNullOrBlank() ||
            dateMLD.value.isNullOrBlank() ||
            min_stockMLD.value.isNullOrBlank() ||
            locationMLD.value.isNullOrBlank()
        ) {
            errorMessage.value = "All fields are required!"
            Toast.makeText(context, "All fields must be filled!", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ If all fields are valid, proceed
        Toast.makeText(context, "Adding Product...", Toast.LENGTH_SHORT).show()

        val product = ProductModel(
            businessId = businessId,
            categoryId = selectedCategoryId!!,
            subcategoryId = selectedSubCategoryId!!,
            name = nameMLD.value.orEmpty(),
            barcode = barcodeMLD.value.orEmpty(),
            saleprice = salepriceMLD.value?.toDoubleOrNull() ?: 0.0,
            salepriceDiscount = dis_sale_priceMLD.value?.toDoubleOrNull() ?: 0.0,
            purchaceprice = purchaseMLD.value?.toDoubleOrNull() ?: 0.0,
            Texes = texesMLD.value?.toDoubleOrNull() ?: 0.0,
            stock = openingstockMLD.value?.toIntOrNull() ?: 0,
            date = dateMLD.value.orEmpty(),
            minstock = min_stockMLD.value?.toIntOrNull() ?: 0,
            itemlocation = locationMLD.value.orEmpty()
        )

        viewModelScope.launch {
            try {
                val response = api.addProduct(product)
                if (response.isSuccessful && response.body() != null) {
                    isSuccessful.value = true

                    Toast.makeText(context, "Product added successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    errorMessage.value = "Failed to add product: ${response.errorBody()?.string()}"
                    Toast.makeText(context, "Failed to add product", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AddProductError", "Exception: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
                Toast.makeText(context, "Error adding product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getBusinessId(): String {
        return sharedPreferences.getString("business_id", "") ?: ""
    }

}
