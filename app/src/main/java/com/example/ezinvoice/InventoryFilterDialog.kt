package com.example.ezinvoice


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.example.ezinvoice.R
import com.example.ezinvoice.apis.addcatageoryapi
import com.example.ezinvoice.models.CatagoryResponce
import com.example.ezinvoice.models.SubCategoryResponse
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryFilterDialog(
    context: Context,
    private val businessId: String,
    private val onApplyFilter: (categoryId: String?, subcategoryId: String?) -> Unit
) : Dialog(context) {

    private lateinit var categorySpinner: Spinner
    private lateinit var subcategorySpinner: Spinner
    private lateinit var applyBtn: Button

    private val categoryMap = mutableMapOf<String, String>() // name -> id
    private val subcategoryMap = mutableMapOf<String, String>() // name -> id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_popup_layout)

        categorySpinner = findViewById(R.id.spinner_category)
        subcategorySpinner = findViewById(R.id.spinner_subcategory)
        applyBtn = findViewById(R.id.btn_apply_filter)

        fetchCategories()

        categorySpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedName = parent.getItemAtPosition(position).toString()
                val categoryId = categoryMap[selectedName]
                if (categoryId != null) fetchSubcategories(categoryId)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                // No-op
            }
        }

        applyBtn.setOnClickListener {
            val catName = categorySpinner.selectedItem?.toString()
            val subCatName = subcategorySpinner.selectedItem?.toString()
            val catId = categoryMap[catName]
            val subCatId = subcategoryMap[subCatName]
            onApplyFilter(catId, subCatId)
            dismiss()
        }
    }

    private fun fetchCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.createService(addcatageoryapi::class.java)
                val response = api.getAllCategory(businessId)
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        val names = categories.map {
                            categoryMap[it.name] = it._id
                            it.name
                        }
                        categorySpinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, names)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchSubcategories(categoryId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.createService(addcatageoryapi::class.java)
                val response = api.getAllSubCategory(categoryId)
                if (response.isSuccessful) {
                    val subcategories = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        val names = subcategories.map {
                            subcategoryMap[it.name] = it._id
                            it.name
                        }
                        subcategorySpinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, names)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
