package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ezinvoice.databinding.ActivityAddCategoryBinding
import com.example.ezinvoice.viewmodels.AddCategoryViewmodel

class AddCategory : AppCompatActivity() {
    private lateinit var databinding:ActivityAddCategoryBinding
    private lateinit var viewmodel:AddCategoryViewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       databinding=DataBindingUtil.setContentView(this,R.layout.activity_add_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewmodel=ViewModelProvider(this)[AddCategoryViewmodel::class.java]
        databinding.lifecycleOwner=this
        databinding.viewmodel=viewmodel

        databinding.appbar.tvTitle.setText("Add Category")
        viewmodel.errorMessage.observe(this){
           Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
        }
        viewmodel.issuccessfull.observe(this){
            if (it) {
                viewmodel.categoryResponse.value?.let {
                    try {

                        Log.e("check category name", "onCreate: ${it?.name}", )

                            val intent = Intent(this@AddCategory, Add_Items::class.java)
                            startActivity(intent)
                            finish()

                    } catch (e: Exception) {
                        Log.e("TAG", "Error while navigating to Business_Info: ${e.localizedMessage}")
                    }
                } ?: Log.e("TAG", "Business data is null")
            }
        }



    }
}