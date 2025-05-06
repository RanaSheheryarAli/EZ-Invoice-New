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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.ezinvoice.apis.BusinessinfoApi
import com.example.ezinvoice.databinding.ActivityMainBinding
import com.example.ezinvoice.models.BusinessInfo
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityMainBinding

    private lateinit var companyName: TextView
    private lateinit var companyEmail: TextView

    private lateinit var companyLogo: ImageView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        enableEdgeToEdge()

//        ViewCompat.setOnApplyWindowInsetsListener(dataBinding.main) { view, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        window.statusBarColor = getColor(R.color.status_bar_color)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE) // ✅ now valid

        userId = sharedPreferences.getString("auth_id", null).toString()
        Log.d("Signup", "user id from main: $userId")

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

                else -> false
            }
        }



        dataBinding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, Sale::class.java))
        }

        dataBinding.headerLayout.menuButton.setOnClickListener {
            toggleDrawer()
        }
    }

//    private fun setupNavigation() {
//        val navController = findNavController(R.id.fragmentContainerView2)
//        dataBinding.bottomnavigation.setupWithNavController(navController)
//
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            dataBinding.headerLayout.tvTitle.text = when (destination.id) {
//                R.id.homeFragment -> "Home"
//                R.id.reportFragment -> "Reports"
//                R.id.itemsFragment -> "Items"
//                R.id.settingFragment -> "Settings"
//                else -> ""
//            }
//        }
//    }

    private fun getBottomNavigationIds() = listOf(
        R.id.homeFragment,
        R.id.reportFragment,
        R.id.itemsFragment,
        R.id.settingFragment
    )

    private fun setupNavigation() {
        dataBinding.bottomnavigation.setupWithNavController(findNavController(R.id.fragmentContainerView2))
//        setupActionBarWithNavController(
//            findNavController(R.id.fragmentContainerView2),
//            AppBarConfiguration(getBottomNavigationIds().toSet())
//        )

        dataBinding.bottomnavigation.setOnNavigationItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, findNavController(R.id.fragmentContainerView2))
            true
        }

        // Setting BottomNavigationView visibility for different screens
        findNavController(R.id.fragmentContainerView2).addOnDestinationChangedListener { _, destination, _ ->
            // Screens which show Bottom Navigation
//            if (getScreenIdsWithBottomNavigation().contains(destination.id)) {
//                if(destination.id==R.id.editDetailsFragment){
//                    dataBinding.imageView2.visibility= View.VISIBLE
//                    dataBinding.moreimg.visibility= View.GONE
//                    dataBinding.textView6.text="Edit"
//                }
//                else{
//                    dataBinding.imageView2.visibility= View.GONE
//                    dataBinding.moreimg.visibility= View.VISIBLE
//                    if(destination.id==R.id.homeFragment){
//                        dataBinding.textView6.text="Home"
//                    }else  if(destination.id==R.id.previewFragment){
//                        dataBinding.textView6.text="Preview"
//                    }else  if(destination.id==R.id.savedFragment){
//                        dataBinding.textView6.text="Saved"
//                    }else  if(destination.id==R.id.accountFragment){
//                        dataBinding.textView6.text="Account"
//                    }
//
//                }
//                showBottomNavigation()
//            }
//            // Screens which hide Bottom Navigation
//            else {
//                hideBottomNavigation()
//            }

            // Adjust ActionBar
//            adjustActionBarForDestination(destination.id)

            // Adjust StatusBar
//            adjustStatusBarColorForDestination(destination.id)
        }
    }

    private fun bindHeader() {
        val navHeaderView: View = dataBinding.navigationView.getHeaderView(0)
        companyName = navHeaderView.findViewById(R.id.textViewCompanyName)
        companyEmail = navHeaderView.findViewById(R.id.textViewEmail)
        companyLogo = navHeaderView.findViewById(R.id.imageViewLogo)
    }


    private fun loadCachedBusinessDetails() {
        val name = sharedPreferences.getString("business_name", null)
        val email = sharedPreferences.getString("business_email", null)
        val logoUrl = sharedPreferences.getString("business_logouri", null)

        if (!name.isNullOrEmpty() && !email.isNullOrEmpty()) {
            companyName.text = name
            companyEmail.text = email
        }

        if (!logoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(logoUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(companyLogo)
        } else {

            Toast.makeText(this, "cash have not logo info", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getBusinessDetailsFromApi(userId: String?) {
        if (userId.isNullOrEmpty()) {
            Log.e("API_ERROR", "User ID is null or empty")
            return
        }

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
                    } ?: Log.e("API_ERROR", "Response body is null")
                } else {
                    Log.e("API_ERROR", "Failed: ${response.code()}")
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

    fun logoutUser() {
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply() // Clears all saved preferences

        val intent = Intent(this, SignIn::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Prevent back navigation
        startActivity(intent)
        finish()
    }

}
