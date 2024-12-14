package com.example.ezinvoice

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.ActivitySaleBinding

class Sale : AppCompatActivity() {

    private  var requestcamear: ActivityResultLauncher<String>?=null
    lateinit var databinding: ActivitySaleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       databinding=DataBindingUtil.setContentView(this,R.layout.activity_sale)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestcamear = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted: Proceed to Barcode_Scanner activity
                val intent = Intent(this, Scan_Barcode::class.java)
                startActivity(intent)
            } else {
                // Permission denied
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "Camera permission is required for scanning barcodes", Toast.LENGTH_SHORT).show()
                } else {
                    // Permission permanently denied, prompt the user to open app settings
                    Toast.makeText(this, "Permission not granted. Please enable it in settings.", Toast.LENGTH_LONG).show()
                    openAppSettings()
                }
            }
        }


        databinding.barcodeScanBtn.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, Scan_Barcode::class.java)
                startActivity(intent)
            } else {
                requestcamear?.launch(android.Manifest.permission.CAMERA)
            }
        }

        databinding.btnSave.setOnClickListener{

        }
    }

    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

}