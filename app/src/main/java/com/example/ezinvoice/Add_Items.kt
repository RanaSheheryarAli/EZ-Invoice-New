package com.example.ezinvoice

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezinvoice.adapters.CategoryAdapter
import com.example.ezinvoice.adaptors.SubCategoryAdapter
import com.example.ezinvoice.apis.addcatageoryapi
import com.example.ezinvoice.databinding.ActivityAddItemsBinding
import com.example.ezinvoice.databinding.CameraBottomSheetBinding
import com.example.ezinvoice.databinding.CatagoryBottomSheetBinding
import com.example.ezinvoice.network.RetrofitClient
import com.example.ezinvoice.viewmodels.AddProductsViewmodel
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Calendar

class Add_Items : AppCompatActivity() {

    private lateinit var databinding: ActivityAddItemsBinding

    private lateinit var addProductViewmodel: AddProductsViewmodel


    private var camera: Camera? = null
    private var isTorchOn = false
    private var isBarcodeDetected = false
    private var scanLineAnimator: ValueAnimator? = null

    private lateinit var bottomSheetBindingForCamera: CameraBottomSheetBinding
    private lateinit var bottomSheetDialogForCamera: BottomSheetDialog

    private lateinit var bottomSheetBindingForCategory: CatagoryBottomSheetBinding
    private lateinit var bottomSheetDialogForCategory: BottomSheetDialog
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var bottomSheetBindingForSubCategory: CatagoryBottomSheetBinding
    private lateinit var bottomSheetDialogForSubCategory: BottomSheetDialog
    private lateinit var subCategoryAdapter: SubCategoryAdapter

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var barcodeDetector: BarcodeDetector

    private lateinit var requestCamera: ActivityResultLauncher<String>
    private lateinit var addSubCategoryLauncher: ActivityResultLauncher<Intent>

    private lateinit var sharedPreferences: SharedPreferences

    private var selectedCategoryId: String? = null
    private var selectedSubCategoryId: String? = null
    private var intentData = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_add_items)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false


        addProductViewmodel = ViewModelProvider(this)[AddProductsViewmodel::class.java]
        databinding.viewmodel = addProductViewmodel
        databinding.lifecycleOwner = this


        // Set calendar icon click listener
        databinding.dateInputLayout.setEndIconOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                databinding.etAsOfDate.setText(formattedDate)
            }, year, month, day)

            datePicker.show()
        }



        databinding.tvTitle.text = "Add Items"
        databinding.btnSave.setOnClickListener {
            if (selectedCategoryId == null || selectedSubCategoryId == null) {
                Toast.makeText(this, "Please select Category and SubCategory", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addProductViewmodel.selectedCategoryId = selectedCategoryId
            addProductViewmodel.selectedSubCategoryId = selectedSubCategoryId
            addProductViewmodel.onSaveProduct(this)
        }

        addProductViewmodel.isSuccessful.observe(this){
            if (it){
                Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show()

                finish()
            }
            else{
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }


        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        mediaPlayer = MediaPlayer.create(this, R.raw.beep)



        setupCameraBottomSheet()
        setupCategoryBottomSheet()
        setupSubCategoryBottomSheet()

        databinding.addcatagoryspinner.setOnClickListener {
            bottomSheetDialogForCategory.show()
        }

        databinding.addsubcatagoryspinner.setOnClickListener {
            if (selectedCategoryId == null) {
                Toast.makeText(this, "Please select a Category first", Toast.LENGTH_SHORT).show()
            } else {
                fetchSubCategories(selectedCategoryId!!)
                Log.e("checkkkk", " checking catagory id${ selectedCategoryId!!}")
                bottomSheetDialogForSubCategory.show()
            }
        }

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

        requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) showCameraBottomSheet()
            else handlePermissionDenied()
        }

        databinding.barcodeScannerIcon.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                showCameraBottomSheet()
            } else {
                requestCamera.launch(android.Manifest.permission.CAMERA)
            }
        }

        addSubCategoryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedCategoryId?.let { fetchSubCategories(it) }
                Toast.makeText(this, "SubCategory added successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCameraBottomSheet() {
        bottomSheetBindingForCamera = CameraBottomSheetBinding.inflate(layoutInflater)
        bottomSheetDialogForCamera = BottomSheetDialog(this)
        bottomSheetDialogForCamera.setContentView(bottomSheetBindingForCamera.root)

        // Add Torch Button Click
        bottomSheetBindingForCamera.torchbtn.setOnClickListener {
            toggleFlashlight()
        }

        // Setup surface view callback
        bottomSheetBindingForCamera.surfaceview.holder.addCallback(object : SurfaceHolder.Callback {
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

                    initializeBarcodeDetector()

                    bottomSheetBindingForCamera.barcodeOverlay.post {
                        startScanLineAnimation()
                    }


                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@Add_Items, "Error opening camera", Toast.LENGTH_SHORT).show()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                releaseCamera()
            }
        })
    }

    private fun showCameraBottomSheet() {
        bottomSheetDialogForCamera.show()
    }

    private fun initializeBarcodeDetector() {
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        if (!barcodeDetector.isOperational) {
            Toast.makeText(this, "Barcode Detector not ready", Toast.LENGTH_SHORT).show()
            return
        }

        camera?.setPreviewCallback { data, cam ->

            if (isBarcodeDetected) return@setPreviewCallback

            val parameters = cam.parameters
            val previewWidth = parameters.previewSize.width
            val previewHeight = parameters.previewSize.height

            val frame = Frame.Builder()
                .setImageData(ByteBuffer.wrap(data), previewWidth, previewHeight, android.graphics.ImageFormat.NV21)
                .build()

            val barcodes = barcodeDetector.detect(frame)

            if (barcodes.size() > 0) {
                val barcode = barcodes.valueAt(0)

                val scanningBox = bottomSheetBindingForCamera.barcodeOverlay.getFrameRect()

                if (scanningBox != null) {
                    val surfaceWidth = bottomSheetBindingForCamera.surfaceview.width
                    val surfaceHeight = bottomSheetBindingForCamera.surfaceview.height

                    val scaleX = surfaceWidth.toFloat() / previewHeight.toFloat() // because rotated
                    val scaleY = surfaceHeight.toFloat() / previewWidth.toFloat()

                    val centerX = (barcode.boundingBox.centerY() * scaleX).toInt()
                    val centerY = (barcode.boundingBox.centerX() * scaleY).toInt()

                    if (scanningBox.contains(centerX, centerY)) {
                        runOnUiThread {
                            mediaPlayer.start()
                            isBarcodeDetected = true
                            databinding.edtBarcode.setText(barcode.displayValue)
                            stopScanLineAnimation()
                            releaseCamera()
                            bottomSheetDialogForCamera.dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun releaseCamera() {
        try {
            stopScanLineAnimation()
            camera?.stopPreview()
            camera?.setPreviewCallback(null)
            camera?.release()
            camera = null
            isTorchOn = false
            isBarcodeDetected = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopScanLineAnimation() {
        scanLineAnimator?.cancel()
        scanLineAnimator = null
        bottomSheetBindingForCamera.scanningLine.visibility = View.INVISIBLE
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
        val scanningBox = bottomSheetBindingForCamera.barcodeOverlay.getFrameRect()

        if (scanningBox == null) {
            bottomSheetBindingForCamera.scanningLine.visibility = View.INVISIBLE
            return
        }

        val surfaceViewTop = bottomSheetBindingForCamera.surfaceview.top
        val absoluteTop = surfaceViewTop + scanningBox.top
        val absoluteBottom = surfaceViewTop + scanningBox.bottom

        val layoutParams = bottomSheetBindingForCamera.scanningLine.layoutParams
        layoutParams.width = scanningBox.width()
        bottomSheetBindingForCamera.scanningLine.layoutParams = layoutParams

        bottomSheetBindingForCamera.scanningLine.x = scanningBox.left.toFloat()
        bottomSheetBindingForCamera.scanningLine.y = absoluteTop.toFloat()

        bottomSheetBindingForCamera.scanningLine.visibility = View.VISIBLE

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
                bottomSheetBindingForCamera.scanningLine.y = animatedValue
            }

            start()
        }
    }

    private fun setupCategoryBottomSheet() {
        bottomSheetBindingForCategory = CatagoryBottomSheetBinding.inflate(layoutInflater)
        bottomSheetDialogForCategory = BottomSheetDialog(this)
        bottomSheetDialogForCategory.setContentView(bottomSheetBindingForCategory.root)

        categoryAdapter = CategoryAdapter { category ->
            databinding.edtAddCatagory.setText(category.name)
            selectedCategoryId = category._id
            selectedSubCategoryId = null
            databinding.edtAddSubcatagory.setText("")
            bottomSheetDialogForCategory.dismiss()
            fetchSubCategories(category._id)
        }

        bottomSheetBindingForCategory.showcategoryitems.layoutManager = LinearLayoutManager(this)
        bottomSheetBindingForCategory.showcategoryitems.adapter = categoryAdapter

        bottomSheetBindingForCategory.addnewcategory.setOnClickListener {
            startActivity(Intent(this, AddCategory::class.java))
            finish()
        }

        bottomSheetBindingForCategory.btncross.setOnClickListener {
            bottomSheetDialogForCategory.dismiss()
        }

        fetchCategories()
    }

    private fun setupSubCategoryBottomSheet() {
        bottomSheetBindingForSubCategory = CatagoryBottomSheetBinding.inflate(layoutInflater)
        bottomSheetDialogForSubCategory = BottomSheetDialog(this)
        bottomSheetDialogForSubCategory.setContentView(bottomSheetBindingForSubCategory.root)

        subCategoryAdapter = SubCategoryAdapter { subCategory ->
            databinding.edtAddSubcatagory.setText(subCategory.name)
            selectedSubCategoryId = subCategory._id
            bottomSheetDialogForSubCategory.dismiss()
        }

        bottomSheetBindingForSubCategory.showcategoryitems.layoutManager = LinearLayoutManager(this)
        bottomSheetBindingForSubCategory.showcategoryitems.adapter = subCategoryAdapter

        bottomSheetBindingForSubCategory.addnewcategory.setOnClickListener {
            val intent = Intent(this, AddSubCategory::class.java)
            intent.putExtra("categoryId", selectedCategoryId)
            startActivity(intent)
            finish()
//            addSubCategoryLauncher.launch(intent)
        }

        bottomSheetBindingForSubCategory.btncross.setOnClickListener {
            bottomSheetDialogForSubCategory.dismiss()
        }
    }


    private fun fetchCategories() {
        val apiService = RetrofitClient.createService(addcatageoryapi::class.java)
        val businessId = sharedPreferences.getString("business_id", "") ?: ""

        if (businessId.isNullOrEmpty()) {
            Toast.makeText(this, "Business ID missing. Please login again!", Toast.LENGTH_SHORT).show()
            return
        }


        Toast.makeText(this, "Business ID: ${businessId}", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            try {
                val response = apiService.getAllCategory(businessId)
                if (response.isSuccessful && response.body() != null) {
                    categoryAdapter.setCategories(response.body()!!)
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error fetching categories: ${e.message}")
            }
        }
    }

    private fun fetchSubCategories(categoryId: String) {
        val apiService = RetrofitClient.createService(addcatageoryapi::class.java)

        lifecycleScope.launch {
            try {
                val response = apiService.getAllSubCategory(categoryId)

                if (response.isSuccessful) {
                    response.body()?.let { subcategories ->
                        subCategoryAdapter.setSubCategories(subcategories)
                    }
                } else {
                    if (response.code() == 404) {
                        // ✅ If no subcategories, just clear adapter
                        subCategoryAdapter.setSubCategories(emptyList())
                        Log.e("API_EMPTY", "No subcategories found.")
                    } else {
                        Log.e("API_FAIL", "API call failed with code: ${response.code()}, error: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Exception while fetching subcategories: ${e.message}")
            }
        }
    }


    private fun handlePermissionDenied() {
        Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
    }

    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}


