package com.example.ezinvoice

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.ActivityBarcodeScannerBinding
import com.example.ezinvoice.databinding.CameraBottomSheetBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.IOException

class Scan_Barcode : AppCompatActivity() {

    private lateinit var databinding: ActivityBarcodeScannerBinding
    private lateinit var bottomSheetBinding: CameraBottomSheetBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    private lateinit var mediaPlayer:MediaPlayer
    private var intent_data = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_barcode_scanner)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.beep)

        // Initialize BottomSheet
        setupBottomSheet()

        // Set the floating action button click listener
        databinding.floatingActionButton.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::cameraSource.isInitialized) {
            cameraSource.release()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::barcodeDetector.isInitialized && !barcodeDetector.isOperational) {
            Toast.makeText(this, "Barcode detector is not operational", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBinding = CameraBottomSheetBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
    }

    private fun showBottomSheetDialog() {
        // Initialize the barcode scanner and camera source
        initializeBarcodeScanner()
        initializeCameraSource()

        // Show the BottomSheetDialog
        bottomSheetDialog.show()
    }

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

                    runOnUiThread {
                        mediaPlayer.start()
                        intent_data = barcodes.valueAt(0).displayValue
                        databinding.name.setText(intent_data) // Display scanned data in main layout

                    }
                }
            }
        })
    }

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

