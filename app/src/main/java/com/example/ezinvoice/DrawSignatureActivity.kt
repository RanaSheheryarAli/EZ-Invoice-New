package com.example.ezinvoice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DrawSignatureActivity : AppCompatActivity() {
    private lateinit var signatureView: SignatureView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_signature)

        signatureView = findViewById(R.id.signature_view)

        findViewById<Button>(R.id.save_signature_btn).setOnClickListener {
            val path = signatureView.saveSignatureToFile(this)
            if (path != null) {
                val intent = Intent().apply {
                    putExtra("signature_path", path)
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        findViewById<Button>(R.id.clear_btn).setOnClickListener {
            signatureView.clear()
        }
    }
}