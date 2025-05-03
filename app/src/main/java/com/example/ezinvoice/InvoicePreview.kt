package com.example.ezinvoice

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ezinvoice.adaptors.InvoicePreviewAdaptor
import com.example.ezinvoice.apis.InvoiceApi
import com.example.ezinvoice.databinding.ActivityInvoicePreviewBinding
import com.example.ezinvoice.models.InvoiceProduct
import com.example.ezinvoice.models.InvoiceRequest
import com.example.ezinvoice.models.ScannedProduct
import com.example.ezinvoice.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class InvoicePreview : AppCompatActivity() {

    private lateinit var binding: ActivityInvoicePreviewBinding
    private var scannedList = mutableListOf<ScannedProduct>()
    private var subtotal = 0.0
    private var totalTax = 0.0

    private var customerName: String = ""
    private var customerPhone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice_preview)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scannedList = intent.getSerializableExtra("scanned_products") as? ArrayList<ScannedProduct>
            ?: mutableListOf()
        customerName = intent.getStringExtra("customer_name") ?: "Customer"
        customerPhone = intent.getStringExtra("customer_phone") ?: ""

        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        binding.invoiceDate.text = currentDate

        setupRecyclerView()
        displayBusinessInfo()
        calculateTotals()
        loadSignature()
        fetchInvoiceNumber()

        binding.btnExport.setOnClickListener {
            createInvoiceAndShare()
        }
    }

    private fun setupRecyclerView() {
        val adapter = InvoicePreviewAdaptor(scannedList)
        binding.recyclerViewItems.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewItems.adapter = adapter
    }

    private fun displayBusinessInfo() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        binding.businessName.text = prefs.getString("business_name", "Business Name")
        binding.businessAddress.text = prefs.getString("business_adress", "Business Address")
        binding.businessPhone.text = "Phone: ${prefs.getString("business_phone", "0000000000")}"
    }

    private fun calculateTotals() {
        for (item in scannedList) {
            val price = item.product.saleprice
            val discount = item.product.salepriceDiscount
            val tax = item.product.Texes
            val qty = item.quantity

            val itemSubtotal = qty * (price - discount)
            val taxAmount = qty * price * tax / 100
            subtotal += itemSubtotal
            totalTax += taxAmount
        }

        val grandTotal = subtotal + totalTax
        binding.subtotal.text = "Subtotal: Rs ${subtotal.toInt()}"
        binding.tax.text = "Tax: Rs ${totalTax.toInt()}"
        binding.totalAmount.text = "Grand Total: Rs ${grandTotal.toInt()}"
    }

    private fun loadSignature() {
        val signaturePath = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            .getString("business_signature", null)

        if (!signaturePath.isNullOrEmpty()) {
            val signatureFile = File(signaturePath)
            if (signatureFile.exists()) {
                try {
                    val uri = FileProvider.getUriForFile(
                        this,
                        "${packageName}.provider",
                        signatureFile
                    )
                    val inputStream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.signatureImage.setImageBitmap(bitmap)
                    inputStream?.close()
                } catch (e: Exception) {
                    Log.e("SIGNATURE_LOAD", "Error loading signature: ${e.message}")
                    Toast.makeText(this, "Failed to load signature", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchInvoiceNumber() {
        val businessId = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            .getString("business_id", "") ?: ""

        val api = RetrofitClient.createService(InvoiceApi::class.java)

        lifecycleScope.launch {
            try {
                val countResponse = api.getTodayInvoiceCount(businessId)
                if (countResponse.isSuccessful) {
                    val count = countResponse.body()?.count ?: 0
                    binding.invoiceNumber.text = "Invoice #${count + 1}"
                } else {
                    binding.invoiceNumber.text = "Invoice #N/A"
                }
            } catch (e: Exception) {
                binding.invoiceNumber.text = "Invoice #Error"
            }
        }
    }

    private fun createInvoiceAndShare() {
        val businessId = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            .getString("business_id", "") ?: ""

        val api = RetrofitClient.createService(InvoiceApi::class.java)
        val productsList = scannedList.map {
            InvoiceProduct(
                productId = it.product._id,
                quantity = it.quantity,
                price = it.product.saleprice
            )
        }

        val invoiceRequest = InvoiceRequest(
            businessId = businessId,
            customerName = customerName,
            customerPhone = customerPhone,
            products = productsList,
            totalAmount = subtotal + totalTax
        )

        lifecycleScope.launch {
            try {
                val response = api.createInvoice(invoiceRequest)
                if (response.isSuccessful) {
                    val file = generatePdfFromInvoice()
                    file?.let {
                        sharePdfViaWhatsApp(it, customerPhone)
                    }
                } else {
                    Toast.makeText(
                        this@InvoicePreview,
                        "Failed to create invoice",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@InvoicePreview, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generatePdfFromInvoice(): File? {
        val invoiceView = binding.scrollView
        val width = invoiceView.width
        val height = invoiceView.height

        val document = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = document.startPage(pageInfo)
        invoiceView.draw(page.canvas)
        document.finishPage(page)

        val fileName = "invoice_${customerName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val invoiceDir = File(downloadsDir, "invoice")
        if (!invoiceDir.exists()) invoiceDir.mkdirs()

        val pdfFile = File(invoiceDir, fileName)

        return try {
            FileOutputStream(pdfFile).use { document.writeTo(it) }
            Toast.makeText(this, "PDF saved: ${pdfFile.absolutePath}", Toast.LENGTH_SHORT).show()
            pdfFile
        } catch (e: IOException) {
            Toast.makeText(this, "Error saving PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            null
        } finally {
            document.close()
        }
    }

    private fun sharePdfViaWhatsApp(pdfFile: File, phoneNumber: String) {
        if (phoneNumber.isBlank()) {
            Toast.makeText(this, "Phone number is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val cleanedNumber = phoneNumber.trim()
            .replace(" ", "")
            .replace("-", "")
            .replaceFirst("^0".toRegex(), "")
        val fullPhone = "92$cleanedNumber"

        val uri = FileProvider.getUriForFile(this, "$packageName.provider", pdfFile)

        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Hi $customerName,\nPlease find your invoice attached.")
                putExtra("jid", "$fullPhone@s.whatsapp.net")
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivityForResult(intent, 101)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
