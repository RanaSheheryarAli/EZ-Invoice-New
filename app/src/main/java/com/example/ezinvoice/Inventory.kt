package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezinvoice.adaptors.ItemAdapter
import com.example.ezinvoice.databinding.ActivityInventoryBinding
import com.example.ezinvoice.viewmodels.ItemsFragmentViewmodel

class Inventory : AppCompatActivity() {

    lateinit var databinding: ActivityInventoryBinding
    lateinit var viewmodel: ItemsFragmentViewmodel
    lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        databinding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(databinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false


        viewmodel = ViewModelProvider(this)[ItemsFragmentViewmodel::class.java]
        itemAdapter = ItemAdapter() // ✅ because ItemsFragment implements OnProductActionListener


        databinding.recyclerInventory.layoutManager = LinearLayoutManager(this)
        databinding.recyclerInventory.adapter = itemAdapter

        viewmodel.fetchProducts()

        viewmodel.productList.observe(this) { products ->
            viewmodel.issuccessfull.observe(this) { success ->
                itemAdapter.setItems(products)

                databinding.tvTotalItems.text = products.size.toString()
                databinding.tvInStock.text = products.sumOf { it.stock }.toString()
                databinding.tvLowStock.text = products.count { it.stock < 5 }.toString()
            }
        }

        databinding.fabAddInventory.setOnClickListener{
            val intent= Intent(this,Add_Items::class.java)
            startActivity(intent)
            finish()
        }
        databinding.ivFilter.setOnClickListener {
            val dialog = InventoryFilterDialog(
                this,
                businessId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("business_id", "") ?: ""
            ) { selectedCategoryId, selectedSubcategoryId ->

                // ✅ Fetch and display filtered products here
                viewmodel.filterProductsByCategory(categoryId = selectedCategoryId, subcategoryId = selectedSubcategoryId)
            }
            dialog.show()
        }

        databinding.etSearchInventory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    viewmodel.fetchProducts()
                } else {
                    viewmodel.searchProducts(query)
                }
            }





            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
