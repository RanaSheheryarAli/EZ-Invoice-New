package com.example.ezinvoice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.ezinvoice.apis.BusinessinfoApi
import com.example.ezinvoice.databinding.ActivityMainBinding
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityMainBinding
    private lateinit var companyName: TextView
    private lateinit var companyEmail: TextView
    private lateinit var companyLogo: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        set status bar color
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)



        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false






        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("auth_id", null).toString()

        setupNavigation()
        bindHeader()
        loadCachedBusinessDetails()
        getBusinessDetailsFromApi(userId)

        dataBinding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    logoutUser()
                    true
                }

                R.id.reportFragment -> {
                    findNavController(R.id.fragmentContainerView2).navigate(R.id.reportFragment)
                    dataBinding.main.closeDrawer(GravityCompat.END)
                    true
                }

                R.id.itemsFragment -> {
                    findNavController(R.id.fragmentContainerView2).navigate(R.id.itemsFragment)
                    dataBinding.main.closeDrawer(GravityCompat.END)
                    true
                }

                R.id.nav_sync -> {
                    bindHeader()
                    loadCachedBusinessDetails()
                    getBusinessDetailsFromApi(userId)

                    Toast.makeText(this, "Business info synced", Toast.LENGTH_SHORT).show()
                    dataBinding.main.closeDrawer(GravityCompat.END)
                    true
                }

                R.id.nav_shareapp -> {
                    shareApp()
                    dataBinding.main.closeDrawer(GravityCompat.END)
                    true
                }

                else -> false
            }
        }

        dataBinding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, Sale::class.java))
        }

        dataBinding.headerLayout.menuButton.setOnClickListener {
            toggleDrawer()
        }
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.fragmentContainerView2)
        dataBinding.bottomnavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            dataBinding.headerLayout.tvTitle.text = when (destination.id) {
                R.id.homeFragment -> "Home"
                R.id.reportFragment -> "Reports"
                R.id.itemsFragment -> "Items"
                R.id.settingFragment -> "Settings"
                else -> ""
            }
        }

        dataBinding.bottomnavigation.setOnNavigationItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, navController)
            true
        }
    }

    private fun bindHeader() {
        val navHeaderView: View = dataBinding.navigationView.getHeaderView(0)
        companyName = navHeaderView.findViewById(R.id.textViewCompanyName)
        companyEmail = navHeaderView.findViewById(R.id.textViewEmail)
        companyLogo = navHeaderView.findViewById(R.id.imageViewLogo)
    }

    private fun loadCachedBusinessDetails() {
        with(sharedPreferences) {
            companyName.text = getString("business_name", null) ?: ""
            companyEmail.text = getString("business_email", null) ?: ""

            val logoUrl = getString("business_logouri", null)
            if (!logoUrl.isNullOrEmpty()) {
                Glide.with(this@MainActivity)
                    .load(logoUrl)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(companyLogo)
            } else {
                Toast.makeText(this@MainActivity, "Cached logo not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getBusinessDetailsFromApi(userId: String?) {
        if (userId.isNullOrEmpty()) return

        val api = RetrofitClient.createService(BusinessinfoApi::class.java)
        lifecycleScope.launch {
            try {
                val response = api.getbusiness(userId)
                if (response.isSuccessful) {
                    response.body()?.let { business ->
                        with(sharedPreferences.edit()) {
                            putString("auth_id", business.userId)
                            putString("business_id", business._id)
                            putString("business_name", business.name)
                            putString("business_owner", business.ownername)
                            putString("business_gstin", business.gstin)
                            putString("business_adress", business.address)
                            putString("business_email", business.email)
                            putString("business_phone", business.contact)
                            putString("business_website", business.website)
                            putString("business_country", business.country)
                            putString("business_currency", business.currency)
                            putString("business_numberformate", business.numberformate)
                            putString("business_dateformate", business.dateformate)
                            putString("business_signature", business.signature)
                            putString("business_logouri", business.logoUrl)
                            apply()
                        }

                        companyName.text = business.name
                        companyEmail.text = business.email

                        if (!business.logoUrl.isNullOrEmpty()) {
                            Glide.with(this@MainActivity)
                                .load(business.logoUrl)
                                .placeholder(R.drawable.logo)
                                .error(R.drawable.logo)
                                .into(companyLogo)
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_FAILURE", "Exception: ${e.localizedMessage}")
            }
        }
    }

    private fun toggleDrawer() {
        if (dataBinding.main.isDrawerOpen(GravityCompat.END)) {
            dataBinding.main.closeDrawer(GravityCompat.END)
        } else {
            dataBinding.main.openDrawer(GravityCompat.END)
        }
    }

    private fun logoutUser() {
        getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, SignIn::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "EZ Invoice")
            putExtra(
                Intent.EXTRA_TEXT,
                "Download the EZ Invoice app to easily create and manage your business invoices. \n\nLink: https://play.google.com/store/apps/details?id=${packageName}"
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Share EZ Invoice via"))
    }


}
