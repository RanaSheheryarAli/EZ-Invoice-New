package com.example.ezinvoice

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ezinvoice.databinding.ActivityBusinessInfoBinding
import com.example.ezinvoice.viewmodels.BusinesssinfoViewmodel

class Business_Info : AppCompatActivity() {

    private lateinit var databinding: ActivityBusinessInfoBinding
    private lateinit var signatureView: SignatureView
    private lateinit var viewModel: BusinesssinfoViewmodel


    // ✅ Correct ViewModel initialization using ViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val TAG="Business info file"
        // ✅ Set up data binding
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_business_info)
        databinding.lifecycleOwner = this



        viewModel= ViewModelProvider(this)[BusinesssinfoViewmodel::class.java]
        databinding.lifecycleOwner = this
        databinding.businessinfoViewmodel = viewModel

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Set status bar color (Check for compatibility)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(R.color.status_bar_color)
        } else {
            Toast.makeText(this, "Build Version is not compatible", Toast.LENGTH_SHORT).show()
        }

        signatureView = databinding.signatureView





        // ✅ Handle button clicks
        databinding.btnSaveSignature.setOnClickListener {
            val filePath = signatureView.saveSignatureToFile()
            if (!filePath.isNullOrEmpty()) {
                Toast.makeText(this, "Signature saved at: $filePath", Toast.LENGTH_LONG).show()
                viewModel.signature1.value = filePath  // ✅ Save file path in ViewModel
            } else {
                Toast.makeText(this, "Failed to save signature", Toast.LENGTH_SHORT).show()
            }
        }

        databinding.btnUpdate.setOnClickListener {
            databinding.executePendingBindings() // Ensure UI updates
            viewModel.onupdateClick()
        }

        databinding.btnClearSignature.setOnClickListener {
            signatureView.clear()
        }

        // ✅ Observe success response
        viewModel.issuccessfull.observe(this) { isSuccess ->
            if (isSuccess) {
                viewModel.businessinfo.value?.let { user ->

                    try {
                        Log.d(TAG, "Sign-in successful, navigating to Business_Info Activity")
                        startActivity(Intent(this@Business_Info, MainActivity::class.java))
                        finish()
                    }
                    catch (e: Exception) {
                        Log.e(TAG, "Error while navigating to Business_Info: ${e.localizedMessage}")
                    }
                }?: Log.e(TAG, "Business data is null")
            }
        }

        // ✅ Observe error messages
        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()  // Clear error after displaying
            }
        }

        databinding.tvAdditionalStting.setOnClickListener {
            databinding.tvGeneralContainer.visibility = View.VISIBLE
        }
    }
}
