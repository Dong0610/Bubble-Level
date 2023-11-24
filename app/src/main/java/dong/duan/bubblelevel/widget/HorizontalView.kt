package dong.duan.bubblelevel.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import dong.duan.bubblelevel.R
import java.lang.Math.toRadians
import kotlin.math.cos

class HorizontalView
    (context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var xPos = 0f
    private var yPos = 0f
    private var center = Point(0, 0)
    private var radius = 0
    private var maxMove = AndroidUtils.dpToPx(50).toInt()
    private var k = (maxMove / (Math.PI / 2)).toFloat()
    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_ver)
    private lateinit var scaledBitmap: Bitmap
    private lateinit var shapeDraw: RectF
    private lateinit var gradient: LinearGradient
    private lateinit var gradient2: LinearGradient
    private var maxAcceleration = maxMove / 10

    var roll = 0f
    var pitch = 0f
    var point: PointF? = null

    private val paintLine = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#301A1A1A")
        strokeWidth = 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = MeasureSpec.getSize(widthMeasureSpec)
            .coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(size, size)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val width = width
        maxMove = width / 2
        k = (maxMove / (Math.PI / 2)).toFloat()
        maxAcceleration = width / 10
        layout(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawShapes(canvas)
    }

    private fun layout(width: Int, height: Int) {
        radius = width / 8
        center.set(width / 2, height / 2)
        shapeDraw = RectF(0f, 0f, width.toFloat(), height.toFloat())
        calculateScaledBitmap()
    }


    private fun calculateScaledBitmap() {
        val desiredBitmapHeight = height * 3 / 4
        val scalingFactor = desiredBitmapHeight.toFloat() / bitmap.height.toFloat()
        val desiredBitmapWidth = (bitmap.width * scalingFactor).toInt()
        scaledBitmap =
            Bitmap.createScaledBitmap(bitmap, desiredBitmapWidth, desiredBitmapHeight, false)
    }

    private fun drawShapes(canvas: Canvas) {
        gradient = LinearGradient(
            shapeDraw.right, shapeDraw.bottom,
            shapeDraw.right, shapeDraw.top,
            Color.parseColor("#7AA714"), Color.parseColor("#B6E822"),
            Shader.TileMode.CLAMP
        )
        gradient2 = LinearGradient(
            0f, 0f, 0f, 0f,
            intArrayOf(
                Color.parseColor("#2F3034"),
                Color.parseColor("#565656"),
                Color.parseColor("#404040")
            ),
            null,
            Shader.TileMode.CLAMP
        )
        pathPaint.apply {
            style = Paint.Style.FILL
            shader = gradient
        }
        canvas.drawRoundRectPath(shapeDraw, 10f, true, true, true, true, pathPaint)

        val paint2 = pathPaint.apply {
            style = Paint.Style.FILL
            shader = gradient2
        }

        val width = canvas.width * 0.02f
        val startRect = RectF(0f, 0f, width, canvas.height.toFloat())
        canvas.drawRoundRectPath(startRect, 10f, true, false, true, false, paint2)

        val endRect =
            RectF(canvas.width - width, 0f, canvas.width.toFloat(), canvas.height.toFloat())
        canvas.drawRoundRectPath(endRect, 10f, false, true, false, true, paint2)

        val firstDr = canvas.width * 0.35f
        val secondDr = firstDr + 15f
        val fourthDr = canvas.width * 0.65f
        val thirdDr = fourthDr - 15f

        paintLine.color = Color.parseColor("#301A1A1A")

        canvas.drawLine(firstDr, 0f, firstDr, canvas.height.toFloat(), paintLine)
        canvas.drawLine(secondDr, 0f, secondDr, canvas.height.toFloat(), paintLine)
        canvas.drawLine(thirdDr, 0f, thirdDr, canvas.height.toFloat(), paintLine)
        canvas.drawLine(fourthDr, 0f, fourthDr, canvas.height.toFloat(), paintLine)

        val x = (center.x - xPos - scaledBitmap.width / 2).coerceIn(
            0f,
            canvas.width - scaledBitmap.width.toFloat()
        )
        val y = (center.y + yPos - scaledBitmap.height / 2).coerceIn(
            0f,
            canvas.height - scaledBitmap.height.toFloat()
        )
        canvas.drawBitmap(scaledBitmap, x, y, pathPaint)
    }

    private val handler = Handler()
    fun updateOrientation(pitch: Float, roll: Float) {
        if (this.pitch != pitch || this.roll != roll) {
            this.pitch = pitch
            this.roll = roll
            val x = width * 0.5f * cos(toRadians(90.0 - roll.toDouble())).toFloat()
            val y = height * 0.5f * cos(toRadians(90.0 - pitch.toDouble())).toFloat()
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
