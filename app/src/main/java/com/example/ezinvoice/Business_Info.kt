package com.example.ezinvoice

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.ezinvoice.databinding.ActivityBusinessInfoBinding
import com.example.ezinvoice.databinding.ActivitySignInBinding

class Business_Info : AppCompatActivity() {
    lateinit var databinding:ActivityBusinessInfoBinding


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


        databinding.btnSaveSignature.setOnClickListener{
            val filePath = signatureView.saveSignatureToFile()
            if (filePath != null) {
                Toast.makeText(this, "Signature saved at: $filePath", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to save signature", Toast.LENGTH_SHORT).show()
            }
        }

        databinding.btnClearSignature.setOnClickListener {
            signatureView.clear()
        }
        databinding.btnUpdate.setOnClickListener{
            val intent= Intent(this@Business_Info,MainActivity::class.java)
            startActivity(intent)
        }

        databinding.tvAdditionalStting.setOnClickListener{
            databinding.tvGeneralContainer.visibility= View.VISIBLE
        }
    }
}