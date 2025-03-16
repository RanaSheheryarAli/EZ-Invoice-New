package com.example.ezinvoice  // Change this to your actual package name

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import java.io.File
import java.io.FileOutputStream

class SignatureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var path = Path()
    private var paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private var parentScrollView: ViewParent? = null

    init {
        setBackgroundColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)  // Disable scrolling
                path.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)  // Enable scrolling
            }
        }
        invalidate()
        return true
    }

    fun clear() {
        path.reset()
        invalidate()
    }

    // Save signature as a transparent PNG
    fun saveSignatureToFile(): String? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) // ARGB supports transparency
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR) // Clear background
        draw(canvas)

        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Signatures")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "signature_${System.currentTimeMillis()}.png")
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // Save with transparency
            outputStream.flush()
            outputStream.close()
            file.absolutePath // Return saved file path
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
