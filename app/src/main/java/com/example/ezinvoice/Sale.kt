package com.example.ezinvoice

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezinvoice.adaptors.SaleAdaptor
import com.example.ezinvoice.databinding.ActivitySaleBinding
import com.example.ezinvoice.models.ScannedProduct

class Sale : AppCompatActivity() {

    private var requestCamera: ActivityResultLauncher<String>? = null
    private lateinit var databinding: ActivitySaleBinding
    private lateinit var adapter: SaleAdaptor
    private var scannedProducts = mutableListOf<ScannedProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_sale)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        databinding.tvTitle.text = "New Invoice"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(R.color.status_bar_color)
        }

        // Receive products from Scan_Barcode
        val receivedList = intent.getSerializableExtra("scanned_products") as? ArrayList<ScannedProduct>
        Log.e("ReceivedList", receivedList.toString())
        if (receivedList != null) {
            scannedProducts.addAll(receivedList)
        }

        setupRecyclerView()
        calculateGrandTotal()

        requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intent = Intent(this, Scan_Barcode::class.java)
                startActivity(intent)
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "Camera permission is required for scanning barcodes", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission not granted. Please enable it in settings.", Toast.LENGTH_LONG).show()
                    openAppSettings()
                }
            }
        }

        databinding.barcodeScanBtn.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, Scan_Barcode::class.java)
                startActivity(intent)
                finish()
            } else {
                requestCamera?.launch(android.Manifest.permission.CAMERA)
            }
        }

        databinding.btnSave.setOnClickListener {
            // 🚀 You can add code to Save Invoice here later
            Toast.makeText(this, "Invoice Saved (Coming Soon...)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = SaleAdaptor(scannedProducts)
        databinding.showscannedproductsRCV.layoutManager = LinearLayoutManager(this)
        databinding.showscannedproductsRCV.adapter = adapter
    }

    private fun calculateGrandTotal() {
        var grandTotal = 0.0

        for (product in scannedProducts) {
            val quantity = product.quantity
            val salePrice = product.product.saleprice
            val discount = product.product.salepriceDiscount
            val taxPercent = product.product.Texes

            // 🔥 NEW formula
            val discountedPrice = salePrice - discount
            val taxAmount = (salePrice * taxPercent) / 100

            val netProductTotal = (quantity * discountedPrice) + (quantity * taxAmount)

            grandTotal += netProductTotal
        }

        databinding.totalAmountText.text = "Rs ${formatPrice(grandTotal)}"
    }

    private fun formatPrice(amount: Double): String {
        val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale("en", "IN"))
        return formatter.format(amount)
    }


    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}
