package com.example.ezinvoice

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class BarcodeOverlay(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val borderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }

    private var frameRect: Rect? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (frameRect == null) {
            val width = width * 3 / 4
            val height = height / 4

            val left = (this.width - width) / 2
            val top = (this.height - height) / 2
            val right = left + width
            val bottom = top + height

            frameRect = Rect(left, top, right, bottom)
        }

        frameRect?.let {
            canvas.drawRect(it, borderPaint)
        }
    }

    fun getFrameRect(): Rect? {
        return frameRect
    }
}
