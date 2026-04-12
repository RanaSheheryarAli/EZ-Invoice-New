package com.example.ezinvoice

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        categoryId = intent.getStringExtra("categoryId")

        viewmodel = ViewModelProvider(this)[AddSubcategoryViewmodel::class.java]
        databinding.lifecycleOwner = this
        databinding.viewmodel = viewmodel

        databinding.tvTitle.text = "Add New"

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
