package com.example.ezinvoice

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.hardware.Camera
import android.media.MediaPlayer
import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezinvoice.adaptors.Scan_BarcodeAdapter
import com.example.ezinvoice.apis.productapi
import com.example.ezinvoice.databinding.ActivityBarcodeScannerBinding
import com.example.ezinvoice.databinding.CameraBottomSheetBinding
import com.example.ezinvoice.models.ProductResponse
import com.example.ezinvoice.network.RetrofitClient
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.ByteBuffer

@Suppress("DEPRECATION")
class Scan_Barcode : AppCompatActivity() {

    private lateinit var databinding: ActivityBarcodeScannerBinding
    private lateinit var bottomSheetBinding: CameraBottomSheetBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var barcodeDetector: BarcodeDetector
    private var camera: Camera? = null
    private var scanLineAnimator: ValueAnimator? = null
    private var isTorchOn = false
    private var isScanningPaused = false

    private val scannedProducts = mutableListOf<ProductResponse>()
    private lateinit var adapter: Scan_BarcodeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databinding = DataBindingUtil.setContentView(this, R.layout.activity_barcode_scanner)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.beep)

        setupRecyclerView()

        databinding.floatingActionButton.setOnClickListener {
            openCameraBottomSheet()
        }
    }

    private fun setupRecyclerView() {
        adapter = Scan_BarcodeAdapter(scannedProducts)
        databinding.showproductsRCV.layoutManager = LinearLayoutManager(this)
        databinding.showproductsRCV.adapter = adapter
    }

    private fun openCameraBottomSheet() {
        bottomSheetBinding = CameraBottomSheetBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetDialog.setOnDismissListener {
            releaseCamera()
        }

        setupCameraInBottomSheet()

        bottomSheetBinding.torchbtn.setOnClickListener {
            toggleFlashlight()
        }

        bottomSheetDialog.show()
    }

    private fun setupCameraInBottomSheet() {
        val holder = bottomSheetBinding.surfaceview.holder
        holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    camera = Camera.open()
                    camera?.setDisplayOrientation(90)

                    val params = camera?.parameters
                    params?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    camera?.parameters = params

                    camera?.setPreviewDisplay(holder)
                    camera?.startPreview()

                    bottomSheetBinding.barcodeOverlay.post {
                        startScanLineAnimation()
                    }

                    setupBarcodeDetector()

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@Scan_Barcode, "Error opening camera", Toast.LENGTH_SHORT).show()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                releaseCamera()
            }
        })
    }

    private fun setupBarcodeDetector() {
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        if (!barcodeDetector.isOperational) {
            Toast.makeText(this, "Barcode Detector not ready", Toast.LENGTH_SHORT).show()
            return
        }

        camera?.setPreviewCallback { data, cam ->

            if (isScanningPaused) return@setPreviewCallback

            val parameters = cam.parameters
            val previewWidth = parameters.previewSize.width
            val previewHeight = parameters.previewSize.height

            val frame = Frame.Builder()
                .setImageData(ByteBuffer.wrap(data), previewWidth, previewHeight, android.graphics.ImageFormat.NV21)
                .build()

            val barcodes = barcodeDetector.detect(frame)

            if (barcodes.size() > 0) {
                val barcode = barcodes.valueAt(0)

                val scanningBox = bottomSheetBinding.barcodeOverlay.getFrameRect()

                if (scanningBox != null) {
                    val surfaceWidth = bottomSheetBinding.surfaceview.width
                    val surfaceHeight = bottomSheetBinding.surfaceview.height

                    val scaleX = surfaceWidth.toFloat() / previewHeight.toFloat()
                    val scaleY = surfaceHeight.toFloat() / previewWidth.toFloat()

                    val centerX = (barcode.boundingBox.centerY() * scaleX).toInt()
                    val centerY = (barcode.boundingBox.centerX() * scaleY).toInt()

                    if (scanningBox.contains(centerX, centerY)) {
                        isScanningPaused = true
                        mediaPlayer.start()
                        searchProductByBarcode(barcode.displayValue)
                    }
                }
            }
        }
    }

    private fun searchProductByBarcode(barcode: String) {
        val api = RetrofitClient.createService(productapi::class.java)

        lifecycleScope.launch {
            try {

                val response = api.getProductByBarcode(barcode)

                if (response.isSuccessful && response.body() != null) {
                    val product = response.body()!!
                    scannedProducts.add(product)
                    adapter.notifyDataSetChanged()

                } else {
                    Toast.makeText(this@Scan_Barcode, "Item not found!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@Scan_Barcode, "Error fetching product", Toast.LENGTH_SHORT).show()
            } finally {
                bottomSheetBinding.surfaceview.postDelayed({
                    isScanningPaused = false
                }, 1000) // 1 second delay

            }
        }
    }

    private fun toggleFlashlight() {
        camera?.let {
            val params = it.parameters
            if (!isTorchOn) {
                params.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                isTorchOn = true
            } else {
                params.flashMode = Camera.Parameters.FLASH_MODE_OFF
                isTorchOn = false
            }
            it.parameters = params
            it.startPreview()
        } ?: Toast.makeText(this, "Camera not ready", Toast.LENGTH_SHORT).show()
    }

    private fun startScanLineAnimation() {
        val scanningBox = bottomSheetBinding.barcodeOverlay.getFrameRect()

        if (scanningBox == null) {
            bottomSheetBinding.scanningLine.visibility = android.view.View.INVISIBLE
            return
        }

        val surfaceViewTop = bottomSheetBinding.surfaceview.top
        val absoluteTop = surfaceViewTop + scanningBox.top
        val absoluteBottom = surfaceViewTop + scanningBox.bottom

        val layoutParams = bottomSheetBinding.scanningLine.layoutParams
        layoutParams.width = scanningBox.width()
        bottomSheetBinding.scanningLine.layoutParams = layoutParams

        bottomSheetBinding.scanningLine.x = scanningBox.left.toFloat()
        bottomSheetBinding.scanningLine.y = absoluteTop.toFloat()

        bottomSheetBinding.scanningLine.visibility = android.view.View.VISIBLE

        scanLineAnimator?.cancel()

        scanLineAnimator = ValueAnimator.ofFloat(
            absoluteTop.toFloat(),
            absoluteBottom.toFloat()
        ).apply {
            duration = 2000
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                bottomSheetBinding.scanningLine.y = animatedValue
            }

            start()
        }
    }

    private fun stopScanLineAnimation() {
        scanLineAnimator?.cancel()
        scanLineAnimator = null
        bottomSheetBinding.scanningLine.visibility = android.view.View.INVISIBLE
    }

    private fun releaseCamera() {
        try {
            stopScanLineAnimation()
            camera?.stopPreview()
            camera?.setPreviewCallback(null)
            camera?.release()
            camera = null
            isTorchOn = false
            isScanningPaused = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
