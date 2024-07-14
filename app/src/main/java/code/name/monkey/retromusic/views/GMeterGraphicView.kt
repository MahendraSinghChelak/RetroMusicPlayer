package code.name.monkey.retromusic.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View

class GMeterGraphicView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val meterThumbPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 2.0f
    }
    private val scaleMarkPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4.0f
    }
    private val roundingMargin = 2
    private var viewWidth = 0f
    private var viewHeight = 0f
    private var baseCenterX = 0f
    private var baseCenterY = 0f
    private var centerX = baseCenterX
    private var centerY = baseCenterY
    private var radius = 30f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = viewWidth - roundingMargin
        canvas.drawCircle(baseCenterX, baseCenterY, width*1/10, scaleMarkPaint)
        canvas.drawCircle(baseCenterX, baseCenterY, width*2/10, scaleMarkPaint)
        canvas.drawCircle(baseCenterX, baseCenterY, width*3/10, scaleMarkPaint)
        canvas.drawCircle(baseCenterX, baseCenterY, width*4/10, scaleMarkPaint)
        canvas.drawCircle(baseCenterX, baseCenterY, width*5/10, scaleMarkPaint)

        canvas.drawCircle(centerX, centerY, radius, meterThumbPaint)
    }

    fun updateMeterPosition(x: Float, y: Float) {
        centerX = baseCenterX + x
        centerY = baseCenterY + y
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
        baseCenterX = viewWidth / 2
        baseCenterY = viewHeight / 2
    }
}
