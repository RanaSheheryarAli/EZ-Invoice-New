package com.example.ezinvoice

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.io.IOException

class Add_Items : AppCompatActivity() {

    private lateinit var databinding: ActivityAddItemsBinding

    private lateinit var addProductViewmodel: AddProductsViewmodel


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
    private lateinit var cameraSource: CameraSource

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(R.color.status_bar_color)
        }


        addProductViewmodel = ViewModelProvider(this)[AddProductsViewmodel::class.java]
        databinding.viewmodel = addProductViewmodel
        databinding.lifecycleOwner = this

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

        databinding.headerLayout.tvTitle.text = "Add Items"

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
    }

    private fun showCameraBottomSheet() {
        initializeBarcodeScanner()
        initializeCameraSource()
        bottomSheetDialogForCamera.show()
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
                    val scannedData = barcodes.valueAt(0).displayValue
                    runOnUiThread {
                        mediaPlayer.start()
                        intentData = scannedData
                        databinding.edtBarcode.setText(intentData)
                        bottomSheetDialogForCamera.dismiss()
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

        bottomSheetBindingForCamera.surfaceview.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource.start(bottomSheetBindingForCamera.surfaceview.holder)
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
        val businessId = sharedPreferences.getString("business-id", "") ?: ""

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


