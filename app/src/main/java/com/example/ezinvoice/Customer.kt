package com.example.ezinvoice

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezinvoice.adapters.ReportAdapterClient
import com.example.ezinvoice.adaptors.CustomerAdaptor
import com.example.ezinvoice.apis.ReportApi
import com.example.ezinvoice.databinding.ActivityClientBinding
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch

class Customer : AppCompatActivity() {

    private lateinit var clientAdapter: CustomerAdaptor
    private lateinit var databinding: ActivityClientBinding
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        databinding = DataBindingUtil.setContentView(this, R.layout.activity_client)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = getColor(R.color.status_bar_color)



        pref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val businessId = pref.getString("business_id", "") ?: ""

        databinding.clientrecyclerView.layoutManager = LinearLayoutManager(this)
        databinding.tvTitle.text = "Customer"

        databinding.syn.setOnClickListener{
            loadTopClients(businessId)
        }
        loadTopClients(businessId)

        databinding.btnAddnew.setOnClickListener{
            val intent= Intent(this@Customer,Sale::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadTopClients(businessId: String) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.createService(ReportApi::class.java)
                val response = api.getTopClients(businessId)
                if (response.isSuccessful) {
                    if(response.body()?.isEmpty() == true){
                        databinding.txNoItemShow.visibility=android.view.View.VISIBLE
                        databinding.containerShowItems.visibility=android.view.View.INVISIBLE
                        val clients = response.body() ?: emptyList()
                        clientAdapter = CustomerAdaptor(clients)
                        databinding.clientrecyclerView.adapter = clientAdapter
                    }
                    else {
                        databinding.txNoItemShow.visibility=android.view.View.INVISIBLE
                        databinding.containerShowItems.visibility=android.view.View.VISIBLE
                        val clients = response.body() ?: emptyList()
                        clientAdapter = CustomerAdaptor(clients)
                        databinding.clientrecyclerView.adapter = clientAdapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
