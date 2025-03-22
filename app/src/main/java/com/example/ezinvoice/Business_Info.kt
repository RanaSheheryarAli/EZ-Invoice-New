package com.example.ezinvoice

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.ezinvoice.databinding.ActivityBusinessInfoBinding
import com.example.ezinvoice.databinding.ActivitySignInBinding
import com.example.ezinvoice.viewmodels.BusinesssinfoViewmodel
import com.example.ezinvoice.viewmodels.SigninViewmodel
import kotlin.math.log

class Business_Info : AppCompatActivity() {
    lateinit var databinding:ActivityBusinessInfoBinding

    private lateinit var Viewmodel: BusinesssinfoViewmodel

    private lateinit var signatureView: SignatureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


      databinding=DataBindingUtil.setContentView(this,R.layout.activity_business_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(R.color.status_bar_color)

        }
        else{
            Toast.makeText(this,"Build Version is not compactable",Toast.LENGTH_SHORT).show()
        }
        signatureView = databinding.signatureView


        Viewmodel = ViewModelProvider(this)[BusinesssinfoViewmodel::class.java]
        databinding.lifecycleOwner = this
        databinding.businessinfoViewmodel = Viewmodel

        Log.e("viewmode init", "onCreate: init view model} ", )

        databinding.btnSaveSignature.setOnClickListener {
            val filePath = signatureView.saveSignatureToFile()
            if (filePath != null) {
                Toast.makeText(this, "Signature saved at: $filePath", Toast.LENGTH_LONG).show()
                Viewmodel.signature1.value = filePath  // ✅ Save file path in ViewModel
            } else {
                Toast.makeText(this, "Failed to save signature", Toast.LENGTH_SHORT).show()
            }
        }


        databinding.btnUpdate.setOnClickListener {
            databinding.executePendingBindings() // Ensure latest UI values are bound

            Viewmodel.onupdateClick()

        }

        databinding.btnClearSignature.setOnClickListener {
            signatureView.clear()
        }


        Viewmodel.issuccessfull.observe(this) { isSuccess ->
            if (isSuccess) {

                val intent = Intent(this@Business_Info, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // ✅ Observe error messages separately to prevent multiple Toasts
        Viewmodel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

                Viewmodel.clearError()  // Clear error after showing
            }
        }

        databinding.tvAdditionalStting.setOnClickListener{
            databinding.tvGeneralContainer.visibility= View.VISIBLE
        }
    }
}