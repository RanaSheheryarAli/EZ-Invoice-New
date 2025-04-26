package com.example.ezinvoice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ezinvoice.apis.AuthApi
import com.example.ezinvoice.apis.BusinessinfoApi
import com.example.ezinvoice.databinding.ActivityMainBinding
import com.example.ezinvoice.models.BusinessInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityMainBinding
    lateinit var  compnayname:TextView
    lateinit var  compnayemail:TextView

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(dataBinding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences=getSharedPreferences("UserPrefs",Context.MODE_PRIVATE)


        window.statusBarColor = getColor(R.color.status_bar_color)
        val navController = findNavController(R.id.fragmentContainerView2)




        dataBinding.bottomnavigation.setupWithNavController(navController)

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("user_id", null)
        val email = sharedPreferences.getString("business_email", null)

        Log.e("useridcheck", "onCreate: ${userid}", )



        val navHeaderView: View = dataBinding.navigationView.getHeaderView(0)

        compnayname=navHeaderView.findViewById(R.id.textViewCompanyName)
        compnayemail  =navHeaderView.findViewById(R.id.textViewEmail)

        getbusinessdetails(userid) // Move API call here


        // Open Navigation Drawer when menu button is clicked

        dataBinding.headerLayout.menuButton.setOnClickListener {
            if (dataBinding.main.isDrawerOpen(GravityCompat.END)) {
                dataBinding.main.closeDrawer(GravityCompat.END)
            } else {
                dataBinding.main.openDrawer(GravityCompat.END)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    dataBinding.headerLayout.tvTitle.text = "Home"
                }

                R.id.reportFragment -> {
                    dataBinding.headerLayout.tvTitle.text = "Reports"
                }

                R.id.itemsFragment -> {
                    dataBinding.headerLayout.tvTitle.text = "Items"
                }

                R.id.settingFragment -> {
                    dataBinding.headerLayout.tvTitle.text = "Settings"
                }

            }
        }

        dataBinding.floatingActionButton.setOnClickListener {
            val intent = Intent(this@MainActivity, Sale::class.java)
            startActivity(intent)
        }


    }


    fun getbusinessdetails(userid:String?){

        if (userid.isNullOrEmpty()) {
            Log.e("API_ERROR", "User ID is null or empty")
            return
        }
        // Retrofit setup
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:5000/api/businesses/")//  Emulator use 10.0.2.2
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(BusinessinfoApi::class.java)


        val businessEmail = sharedPreferences.getString("business-email", null)
        val businessName = sharedPreferences.getString("business-name", null)


        if(businessName == null && businessEmail==null){
            val call =api.getbusiness(userid)

            call.enqueue(object : Callback<BusinessInfo>{
                override fun onResponse(call: Call<BusinessInfo>, response: Response<BusinessInfo>) {
                    if (response.isSuccessful) {
                        val businessinfo = response.body()
                        businessinfo?.let {
                            Log.d("API_SUCCESS", "Business Name: ${it.name}, Address: ${it.address}")

                            sharedPreferences.edit().putString("business-id", response.body()?._id).apply()
                            sharedPreferences.edit().putString("business-name", response.body()?.name).apply()
                            sharedPreferences.edit().putString("business-email", response.body()?.email).apply()
                            // Update UI
                            compnayname.text = it.name
                            compnayemail.text = it.email

                        } ?: Log.e("API_ERROR", "Response body is null")
                    } else {
                        Log.e("API_ERROR", "Failed with response code: ${response.code()}")
                    }
                }


                override fun onFailure(call: Call<BusinessInfo>, t: Throwable) {
                    Log.e("API_FAILURE", "API call failed: Network Error")
                }

            })
        }

        else{
            compnayname.text = businessName
            compnayemail.text = businessEmail
        }





    }
}
