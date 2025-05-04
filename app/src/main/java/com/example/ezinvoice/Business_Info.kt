package com.example.ezinvoice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.example.ezinvoice.apis.UploadImgApi
import com.example.ezinvoice.databinding.ActivityBusinessInfoBinding
import com.example.ezinvoice.viewmodels.BusinesssinfoViewmodel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import android.database.Cursor
import android.provider.OpenableColumns
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*

class Business_Info : AppCompatActivity() {

    private lateinit var databinding: ActivityBusinessInfoBinding
    private lateinit var signatureView: SignatureView
    private lateinit var viewModel: BusinesssinfoViewmodel
    private val TAG = "BusinessInfoActivity"

    // Permission request launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val correctedBitmap = handleImageOrientation(this, it)
                databinding.imgAddLogo.setImageBitmap(correctedBitmap)
                databinding.tvAddLogo.visibility = View.GONE
                viewModel.logoLocalUri.value = it
                viewModel.uploadLogoImage(this@Business_Info) // ✅ Correct context passed
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

        window.statusBarColor = getColor(R.color.status_bar_color)


        val currencyList = listOf("PKR (Rs)", "USD ($)", "EUR (€)", "GBP (£)")
        val countryList = listOf("Pakistan", "USA", "UK", "Germany")
        val numberFormatList = listOf("1,000,000", "1.000.000", "1 000 000")
        val dateFormatList = listOf("DD-MM-YYYY", "MM/DD/YYYY", "YYYY.MM.DD")

        fun setupDropdown(viewId: AutoCompleteTextView, items: List<String>) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
            viewId.setAdapter(adapter)
        }

        setupDropdown(databinding.autoCurrency, currencyList)
        setupDropdown(databinding.autoCountry, countryList)
        setupDropdown(databinding.autoNumberFormat, numberFormatList)
        setupDropdown(databinding.autoDateFormat, dateFormatList)


        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(R.color.status_bar_color)
        } else {
            Toast.makeText(this, "Build Version is not compatible", Toast.LENGTH_SHORT).show()
        }

        signatureView = databinding.signatureView

        databinding.icAddIcon.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }


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
            if (viewModel.logoUri.value.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a logo image first", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.onupdateClick() // ✅ Use already uploaded image URL
            }
        }

        databinding.autoCurrency.setOnItemClickListener { parent, _, position, _ ->
            viewModel.currency.value = parent.getItemAtPosition(position).toString()
        }

        databinding.autoCountry.setOnItemClickListener { parent, _, position, _ ->
            viewModel.country.value = parent.getItemAtPosition(position).toString()
        }

        databinding.autoNumberFormat.setOnItemClickListener { parent, _, position, _ ->
            viewModel.numberformate.value = parent.getItemAtPosition(position).toString()
        }

        databinding.autoDateFormat.setOnItemClickListener { parent, _, position, _ ->
            viewModel.dateformate.value = parent.getItemAtPosition(position).toString()
        }

        databinding.btnClearSignature.setOnClickListener {
            signatureView.clear()
        }

        // Observe success response
        viewModel.issuccessfull.observe(this) { isSuccess ->
            if (isSuccess) {

                Log.d(TAG, "Sign-in successful, navigating to MainActivity")
                val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val authId = prefs.getString("auth_id", null)

                if (!authId.isNullOrEmpty()) {
                    Log.d("Business_Info", "auth_id passed to main: $authId")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("Business_Info", "auth_id is missing before moving to MainActivity")
                    startActivity(Intent(this@Business_Info, SignIn::class.java))
                    finish()
                }
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

    fun handleImageOrientation(context: Context, uri: Uri): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val exif = ExifInterface(context.contentResolver.openInputStream(uri)!!)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }


    private fun saveSignature() {
        val signatureBitmap = signatureView.getSignatureBitmap()

        val signatureDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Signatures"
        )

        if (!signatureDir.exists()) {
            signatureDir.mkdirs()
        }

        val fileName = "signature_${System.currentTimeMillis()}.png"
        val signatureFile = File(signatureDir, fileName)

        try {
            val outputStream = FileOutputStream(signatureFile)
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Notify gallery (optional)
            sendBroadcast(
                Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(signatureFile)
                )
            )

            // Also update SharedPreferences directly
            val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            prefs.edit().putString("business_signature", signatureFile.absolutePath).apply()

            Toast.makeText(this, "Signature saved in Downloads/Signatures", Toast.LENGTH_SHORT)
                .show()

            // Save path to ViewModel
            viewModel.signature1.value = signatureFile.absolutePath

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save signature: ${e.message}", Toast.LENGTH_LONG).show()
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