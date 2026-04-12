package com.example.ezinvoice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.ezinvoice.databinding.ActivitySignatureBinding
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.File

class Signature : AppCompatActivity() {

    private lateinit var databinding: ActivitySignatureBinding

    private val GALLERY_REQUEST = 101
    private val CAMERA_REQUEST = 102
    private val SIGN_REQUEST = 103
    private var imageUri: Uri? = null
    private var latestSignaturePath: String? = null  // ✅ To track the latest path

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_signature)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        // ✅ Load previously saved signature
        val savedPath = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            .getString("business_signature", null)

        if (!savedPath.isNullOrEmpty()) {
            val imageFile = File(savedPath)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(savedPath)
                databinding.signatureImage.setImageBitmap(bitmap)
            }
        }


        databinding.backButton.setOnClickListener { finish() }

        databinding.addSignatureButton.setOnClickListener {
            if (checkAndRequestPermissions()) {
                showAddSignatureDialog()
            }
        }

        databinding.doneButton.setOnClickListener {
            val signaturePath = latestSignaturePath

            if (signaturePath.isNullOrEmpty()) {
                Toast.makeText(this, "No signature to save", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Save to SharedPreferences
            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .edit().putString("business_signature", signaturePath).apply()

            // ✅ Upload to backend
            val userId = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("auth_id", "") ?: ""

            updateSignatureToBackend(userId, signaturePath)
        }
    }

    private fun showAddSignatureDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_signature, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<LinearLayout>(R.id.sign_now).setOnClickListener {
            openDrawSignature()
            dialog.dismiss()
        }

        dialogView.findViewById<LinearLayout>(R.id.choose_gallery).setOnClickListener {
            openGallery()
            dialog.dismiss()
        }

        dialogView.findViewById<LinearLayout>(R.id.take_photo).setOnClickListener {
            openCamera()
            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.cancel_btn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    private fun openCamera() {
        try {
            val imageFile = File.createTempFile(
                "signature_${System.currentTimeMillis()}",
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )

            imageUri = FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                imageFile
            )

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, CAMERA_REQUEST)
            } else {
                Toast.makeText(this, "No camera app found.", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error opening camera: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun openDrawSignature() {
        val intent = Intent(this, DrawSignatureActivity::class.java)
        startActivityForResult(intent, SIGN_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GALLERY_REQUEST -> {
                if (resultCode == RESULT_OK && data != null) {
                    val selectedImageUri = data.data
                    databinding.signatureImage.setImageURI(selectedImageUri)
                    latestSignaturePath = getRealPathFromURI(selectedImageUri)
                }
            }

            CAMERA_REQUEST -> {
                if (resultCode == RESULT_OK && imageUri != null) {
                    try {
                        val stream = contentResolver.openInputStream(imageUri!!)
                        val bitmap = BitmapFactory.decodeStream(stream)
                        databinding.signatureImage.setImageBitmap(bitmap)
                        stream?.close()
                        latestSignaturePath = imageUri?.path
                    } catch (e: Exception) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SIGN_REQUEST -> {
                if (resultCode == RESULT_OK && data != null) {
                    val path = data.getStringExtra("signature_path")
                    if (!path.isNullOrEmpty()) {
                        val bitmap = BitmapFactory.decodeFile(path)
                        databinding.signatureImage.setImageBitmap(bitmap)
                        latestSignaturePath = path
                    }
                }
            }
        }
    }

    private fun updateSignatureToBackend(userId: String, signatureUrl: String) {
        val api = RetrofitClient.createService(com.example.ezinvoice.apis.BusinessinfoApi::class.java)
        val payload = mapOf("signature" to signatureUrl)

        lifecycleScope.launch {
            try {
                val response = api.updateBusiness(userId, payload)
                if (response.isSuccessful) {
                    Toast.makeText(this@Signature, "Signature uploaded to server", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@Signature, "Server error: $errorBody", Toast.LENGTH_LONG).show()

//                    Toast.makeText(this@Signature, "Failed to update signature", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Signature, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        return if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 1000)
            false
        } else {
            true
        }
    }

    // Optional: For gallery path resolution (API < 30)
    private fun getRealPathFromURI(uri: Uri?): String? {
        if (uri == null) return null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        }
    }
}
