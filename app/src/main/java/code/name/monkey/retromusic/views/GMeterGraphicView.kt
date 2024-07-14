package code.name.monkey.retromusic.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View

class GMeterGraphicView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val meterFillPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        setShadowLayer(10f, 5f, 5f, Color.BLACK)
    }
    private val meterStrokePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 3.0f
    }
    private val valueLinePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private val transparentBlackPaint = Paint().apply {
        color = Color.argb(71, 0, 0, 0)
        style = Paint.Style.FILL
    }
    private val scaleMarkPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4.0f
        maskFilter = BlurMaskFilter(7f, BlurMaskFilter.Blur.SOLID)
    }
    private lateinit var backgroundBitmap: Bitmap

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

        backgroundBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }

        canvas.drawLine(0.0f, centerY, viewWidth, centerY, valueLinePaint)
        canvas.drawLine(centerX, 0.0f, centerX, viewHeight, valueLinePaint)

        canvas.drawCircle(centerX, centerY, radius, meterFillPaint)
        canvas.drawCircle(centerX, centerY, radius, meterStrokePaint)
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

        backgroundBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            val width = w - roundingMargin

            canvas.drawCircle(baseCenterX, baseCenterY, width/2f, transparentBlackPaint)

            canvas.drawCircle(baseCenterX, baseCenterY, width*1/10f, scaleMarkPaint)
            canvas.drawCircle(baseCenterX, baseCenterY, width*2/10f, scaleMarkPaint)
            canvas.drawCircle(baseCenterX, baseCenterY, width*3/10f, scaleMarkPaint)
            canvas.drawCircle(baseCenterX, baseCenterY, width*4/10f, scaleMarkPaint)
            canvas.drawCircle(baseCenterX, baseCenterY, width*5/10f, scaleMarkPaint)

        }
    }
}
