package com.example.ezinvoice

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ezinvoice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(dataBinding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navController = findNavController(R.id.fragmentContainerView2)

        dataBinding.bottomnavigation.setupWithNavController(navController)

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
}
