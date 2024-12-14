package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.ActivityBusinessInfoBinding
import com.example.ezinvoice.databinding.ActivitySignInBinding

class Business_Info : AppCompatActivity() {
    lateinit var databinding:ActivityBusinessInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
      databinding=DataBindingUtil.setContentView(this,R.layout.activity_business_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        databinding.btnUpdate.setOnClickListener{
            val intent= Intent(this@Business_Info,MainActivity::class.java)
            startActivity(intent)
        }

        databinding.tvAdditionalStting.setOnClickListener{
            databinding.tvGeneralContainer.visibility= View.VISIBLE
        }
    }
}