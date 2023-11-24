package dong.duan.bubblelevel.widget

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import dong.duan.bubblelevel.R

class VerticalView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val pathPaint: Paint
    private var xPos = 0f
    private var yPos = 0f
    private lateinit var shapeDraw: RectF
    private val center: Point
    private var radius = 0
    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_hor)
    private lateinit var scaledBitmap: Bitmap
    private var maxMove = AndroidUtils.dpToPx(50).toInt()
    private var k = (maxMove / (Math.PI / 2)).toFloat()
    private var maxAcceleration = maxMove / 3
    private val paintLine = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#301A1A1A")
        strokeWidth = 2f
    }

    var roll = 0f
    var pitch = 0f
    var point: PointF? = null

    init {
        pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pathPaint.strokeWidth = 1f
        pathPaint.style = Paint.Style.STROKE
        pathPaint.color = Color.WHITE
        center = Point(0, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var w = MeasureSpec.getSize(widthMeasureSpec)
        var h = MeasureSpec.getSize(heightMeasureSpec)
        if (w > h) {
            w = h
        } else {
            h = w
        }
        setMeasuredDimension(
            resolveSize(w, widthMeasureSpec),
            resolveSize(h, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val height = height
        maxMove = height / 2
        k = (maxMove / (Math.PI / 2)).toFloat()
        maxAcceleration = height / 3
        layout(width, height)
    }

    private fun layout(width: Int, height: Int) {
        radius = width / 8
        center.set(width / 2, height / 2)
        shapeDraw = RectF(0f, 0f, width.toFloat(), height.toFloat())
        calculateScaledBitmap()
    }

    private fun calculateScaledBitmap() {
        val desiredBitmapWidth = width * 3 / 4
        val scalingFactor = desiredBitmapWidth.toFloat() / bitmap.height
        val desiredBitmapHeight = (bitmap.height * scalingFactor).toInt()
        scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredBitmapWidth, desiredBitmapHeight, false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawShapes(canvas)
    }

    private fun drawShapes(canvas: Canvas) {
        shapeDraw = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
        val gradient = LinearGradient(
            shapeDraw.left, shapeDraw.centerY(),
            shapeDraw.right, shapeDraw.centerY(),
            Color.parseColor("#7AA714"), Color.parseColor("#B6E822"),
            Shader.TileMode.CLAMP
        )
        pathPaint.apply {
            style = Paint.Style.FILL
            shader = gradient
        }

        canvas.drawRoundRectPath(shapeDraw, 10f, true, true, true, true, pathPaint)

        val gradient2 = LinearGradient(
            shapeDraw.left, shapeDraw.centerY(),
            shapeDraw.right, shapeDraw.centerY(),
            intArrayOf(
                Color.parseColor("#2F3034"),
                Color.parseColor("#565656"),
                Color.parseColor("#404040")
            ),
            null,
            Shader.TileMode.CLAMP
        )
        val paint2 = pathPaint
        paint2.apply {
            style = Paint.Style.FILL
            shader = gradient2
        }

        val x = (center.x - xPos - scaledBitmap.width / 2 ).coerceIn(0f, canvas.width - scaledBitmap.width.toFloat())
        val y = (center.y + yPos - scaledBitmap.height / 2).coerceIn(0f, canvas.height - scaledBitmap.height.toFloat())
        canvas.drawBitmap(scaledBitmap, x, y, pathPaint)

        var firstDr = canvas.height * 0.35f
        var secontDr = firstDr + 15f
        var fourDr = canvas.height * 0.65f
        var threeDr = fourDr - 15f

        paintLine.color =Color.parseColor("#301A1A1A")

        canvas.drawLine(0f, firstDr, canvas.width.toFloat(), firstDr, paintLine)
        canvas.drawLine(0f, secontDr, canvas.width.toFloat(), secontDr, paintLine)
        canvas.drawLine(0f, threeDr, canvas.width.toFloat(), threeDr, paintLine)
        canvas.drawLine(0f, fourDr, canvas.width.toFloat(), fourDr, paintLine)

        val height = canvas.height * 0.02f
        val startRect = RectF(0f, 0f, canvas.width.toFloat(), height)
        canvas.drawRoundRectPath(startRect, 10f, true, true, false, false, paint2)

        val endRect = RectF(0f, canvas.height - height, canvas.width.toFloat(), canvas.height.toFloat())
        canvas.drawRoundRectPath(endRect, 10f, false, false, true, true, paint2)
    }
    private val handler = Handler()
    fun updateOrientation(newPitch: Float, newRoll: Float) {
        if (newPitch != pitch || newRoll != roll) {
            pitch = newPitch
            roll = newRoll
            val x = width * 0.5f * cos(Math.toRadians(90.0 - roll.toDouble())).toFloat()
            val y = height * 0.5f * cos(Math.toRadians(90.0 - pitch.toDouble())).toFloat()
            point = PointF(x, y)
            update(point)
            invalidate()
            handler.postDelayed({
                update(point)
                invalidate()
            }, 400)
        }
    }

    private fun update(value: PointF?) {
        xPos = value?.x ?: 0f
        yPos = value?.y ?: 0f
    }
}
