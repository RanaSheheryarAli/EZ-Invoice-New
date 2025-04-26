package com.example.ezinvoice

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ezinvoice.databinding.ActivityAddSubCategoryBinding
import com.example.ezinvoice.viewmodels.AddSubcategoryViewmodel

class AddSubCategory : AppCompatActivity() {

    private lateinit var databinding: ActivityAddSubCategoryBinding
    private lateinit var viewmodel: AddSubcategoryViewmodel
    private var categoryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        databinding = DataBindingUtil.setContentView(this, R.layout.activity_add_sub_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryId = intent.getStringExtra("categoryId")

        viewmodel = ViewModelProvider(this)[AddSubcategoryViewmodel::class.java]
        databinding.lifecycleOwner = this
        databinding.viewmodel = viewmodel

        databinding.appbar.tvTitle.text = "Add SubCategory"

        viewmodel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewmodel.isSuccessful.observe(this) {
            if (it) {
                setResult(RESULT_OK)
                finish()
            }
        }

        viewmodel.categoryId.value = categoryId
    }
}
