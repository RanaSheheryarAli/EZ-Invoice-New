package com.example.ezinvoice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ezinvoice.databinding.ActivityBusinessInfoBinding
import com.example.ezinvoice.viewmodels.BusinesssinfoViewmodel
import java.io.File

class Business_Info : AppCompatActivity() {

    private lateinit var databinding: ActivityBusinessInfoBinding
    private lateinit var signatureView: SignatureView
    private lateinit var viewModel: BusinesssinfoViewmodel
    private val TAG = "BusinessInfoActivity"

    // Permission request launcher
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            saveSignature()
        } else {
            Toast.makeText(
                this,
                "Permission denied. Cannot save signature.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set up data binding
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_business_info)
        databinding.lifecycleOwner = this

        viewModel = ViewModelProvider(this)[BusinesssinfoViewmodel::class.java]
        databinding.businessinfoViewmodel = viewModel

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(R.color.status_bar_color)
        } else {
            Toast.makeText(this, "Build Version is not compatible", Toast.LENGTH_SHORT).show()
        }

        signatureView = databinding.signatureView

        // Handle button clicks
        databinding.btnSaveSignature.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        saveSignature()
                    }

                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                        showPermissionRationale()
                    }

                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            } else {
                saveSignature()
            }
        }

        databinding.btnUpdate.setOnClickListener {
            databinding.executePendingBindings() // Ensure UI updates
            viewModel.onupdateClick()
        }

        databinding.btnClearSignature.setOnClickListener {
            signatureView.clear()
        }

        // Observe success response
        viewModel.issuccessfull.observe(this) { isSuccess ->
            if (isSuccess) {
                viewModel.businessinfo.value?.let { user ->
                    try {
                        Log.d(TAG, "Sign-in successful, navigating to MainActivity")
                        startActivity(Intent(this@Business_Info, MainActivity::class.java))
                        finish()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error while navigating to MainActivity: ${e.localizedMessage}")
                    }
                } ?: Log.e(TAG, "Business data is null")
            }
        }

        // Observe error messages
        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        databinding.tvAdditionalStting.setOnClickListener {
            databinding.tvGeneralContainer.visibility = View.VISIBLE
        }
    }

    private fun saveSignature() {
        val filePath = signatureView.saveSignatureToFile(this@Business_Info)
        if (!filePath.isNullOrEmpty()) {
            Toast.makeText(this, "Signature saved successfully", Toast.LENGTH_SHORT).show()
            viewModel.signature1.value = filePath
        } else {
            Toast.makeText(
                this,
                "Failed to save signature. Check storage permissions.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showPermissionRationale() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Storage Permission Needed")
            .setMessage("This permission is required to save your signature")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}