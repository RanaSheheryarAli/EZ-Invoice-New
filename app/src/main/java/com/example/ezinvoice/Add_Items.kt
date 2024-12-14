package com.example.ezinvoice

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.ActivityAddItemsBinding
import com.example.ezinvoice.databinding.CameraBottomSheetBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.IOException

class Add_Items : AppCompatActivity() {

    private lateinit var requestCamera: ActivityResultLauncher<String>
    private lateinit var databinding: ActivityAddItemsBinding

    private lateinit var bottomSheetBinding: CameraBottomSheetBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    private lateinit var mediaPlayer: MediaPlayer
    private var intentData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_add_items)

        // Initialize MediaPlayer with beep sound
        mediaPlayer = MediaPlayer.create(this, R.raw.beep)

        // Handle edge-to-edge system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomSheet()

        // Register camera permission request callback
        requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                showBottomSheetDialog() // Permission granted, open scanner
            } else {
                handlePermissionDenied()
            }
        }

        // Set header title
        databinding.headerLayout.tvTitle.text = "Add Items"

        // Barcode scanner icon click
        databinding.barcodeScannerIcon.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                showBottomSheetDialog()
            } else {
                requestCamera.launch(android.Manifest.permission.CAMERA)
            }
        }

        // Pricing button click logic
        databinding.btnPricing.setOnClickListener {
            databinding.apply {
                tvPriceLine.visibility = View.VISIBLE
                tvStockLine.visibility = View.GONE
                containerStock.visibility = View.GONE
                containerSale.visibility = View.VISIBLE
                containerPurchase.visibility = View.VISIBLE
                containerTexes.visibility = View.VISIBLE
            }
        }

        // Stock button click logic
        databinding.btnStock.setOnClickListener {
            databinding.apply {
                containerStock.visibility = View.VISIBLE
                tvPriceLine.visibility = View.GONE
                tvStockLine.visibility = View.VISIBLE
                containerSale.visibility = View.GONE
                containerPurchase.visibility = View.GONE
                containerTexes.visibility = View.GONE
            }
        }
    }

    // Release MediaPlayer when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    // Handle denied permission
    private fun handlePermissionDenied() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Camera permission is required for scanning barcodes", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission not granted. Please enable it in settings.", Toast.LENGTH_LONG).show()
            openAppSettings()
        }
    }

    // Open app settings for manual permission grant
    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    // Setup the bottom sheet for the scanner
    private fun setupBottomSheet() {
        bottomSheetBinding = CameraBottomSheetBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
    }

    // Show the barcode scanner dialog
    private fun showBottomSheetDialog() {
        initializeBarcodeScanner()
        initializeCameraSource()
        bottomSheetDialog.show()
    }

    // Initialize the barcode detector
    private fun initializeBarcodeScanner() {
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Barcode scanner stopped", Toast.LENGTH_SHORT).show()
            }

            @SuppressLint("SetTextI18n")
            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    val scannedData = barcodes.valueAt(0).displayValue
                    runOnUiThread {
                        // Play the sound
                        mediaPlayer.start()

                        // Update UI
                        intentData = scannedData
                        databinding.edtBarcode.setText(intentData) // Update main layout
//                        bottomSheetBinding.barcodeVale.text = intentData // Update bottom sheet
                        bottomSheetDialog.dismiss() // Close the dialog
                    }
                }
            }
        })
    }

    // Initialize the camera source
    private fun initializeCameraSource() {
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .build()

        bottomSheetBinding.surfaceview.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource.start(bottomSheetBinding.surfaceview.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
    }
}
