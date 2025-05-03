package com.example.ezinvoice

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Environment
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SignatureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var path = Path()
    private var paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private var lastTouchX = 0f
    private var lastTouchY = 0f

    init {
        setBackgroundColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(touchX, touchY)
                lastTouchX = touchX
                lastTouchY = touchY
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(touchX - lastTouchX)
                val dy = Math.abs(touchY - lastTouchY)
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    path.quadTo(lastTouchX, lastTouchY, (touchX + lastTouchX) / 2, (touchY + lastTouchY) / 2)
                    lastTouchX = touchX
                    lastTouchY = touchY
                }
            }
            MotionEvent.ACTION_UP -> {
                path.lineTo(lastTouchX, lastTouchY)
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }

        invalidate()
        return true
    }

    fun clear() {
        path.reset()
        invalidate()
    }

    fun saveSignatureToFile(context: Context): String? {
        if (width == 0 || height == 0) {
            return null
        }

        return try {
            // Create bitmap
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                eraseColor(Color.TRANSPARENT)
            }
            val canvas = Canvas(bitmap)
            draw(canvas)

            // Get storage directory
            val storageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            } else {
                File(Environment.getExternalStorageDirectory(), "Pictures")
            }

            // Create signatures directory if needed
            val signaturesDir = File(storageDir, "Signatures").apply {
                if (!exists()) {
                    mkdirs()
                }
            }

            // Create the file
            val file = File(signaturesDir, "signature_${System.currentTimeMillis()}.png")

            // Save the bitmap
            FileOutputStream(file).use { out ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    return null
                }
                out.flush()
            }

            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }

    fun getSignatureBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

}