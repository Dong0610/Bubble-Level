package dong.duan.bubblelevel.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import android.os.Handler
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import dong.duan.bubblelevel.R
import kotlin.math.cos

open class CricleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val pathPaint: Paint
    private var path: Path? = null
    private var xPos = 0f
    private var yPos = 0f
    private val center: Point
    private var radius = 0
    private var rectF = RectF()
    private val isSimple = false
    private val bitmapBg =
        BitmapFactory.decodeResource(context.resources, R.drawable.img_center_view)
    private val bitmapBall =
        BitmapFactory.decodeResource(context.resources, R.drawable.img_center_ball)
    private var scaledBitmap: Bitmap? = null

    init {
        val gridColor: Int

        val typedValue = TypedValue()
        val theme = context.theme
        gridColor = if (theme.resolveAttribute(R.attr.accelerometerGridColor, typedValue, true)) {
            typedValue.data
        } else {
            ContextCompat.getColor(context, R.color.white)
        }

        pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pathPaint.strokeWidth = 1f
        pathPaint.style = Paint.Style.STROKE
        pathPaint.color = gridColor
        center = Point(0, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = MeasureSpec.getSize(widthMeasureSpec)
            .coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(size, size)
    }

    var gradient: LinearGradient? = null
    var maxAcceleration = 0
    var paintRect: Paint? = null
    val shapePath = Path()
    var bitmapCenterScale: Bitmap? = null
    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val height = height
        maxMove = height / 2
        k = (maxMove / (Math.PI / 2)).toFloat()
        maxAcceleration = height / 3
        layout(width, height)
        scaledBitmap = Bitmap.createScaledBitmap(
            bitmapBg,
            (width - 32f).toInt(),
            (height - 32f).toInt(), false
        )

        gradient = LinearGradient(
            bitmapX + scaledBitmap!!.width, bitmapY + scaledBitmap!!.height, bitmapX, bitmapY,
            intArrayOf(
                Color.parseColor("#050505"),
                Color.parseColor("#565656")
            ),
            null,
            Shader.TileMode.CLAMP
        )
        paintRect = Paint().apply {
            style = Paint.Style.FILL
            shader = gradient
        }

        val bubbleCenterW = width / 5
        bitmapCenterScale =
            Bitmap.createScaledBitmap(bitmapBall, bubbleCenterW, bubbleCenterW, false)


    }

    private fun layout(width: Int, height: Int) {
        radius = width / 8
        center.set(width / 2, height / 2)
        if (path == null) {
            val radius = width / 2f - width * 0.03f
            path = Path()
            path!!.moveTo(center.x - radius, center.y.toFloat())
            path!!.lineTo(center.x + radius, center.y.toFloat())
            path!!.moveTo(center.x.toFloat(), center.y - radius)
            path!!.lineTo(center.x.toFloat(), center.y + radius)
            if (!isSimple) {
                rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            }
        }
    }

    val bitmapX = 16f
    val bitmapY = 16f

    private fun drawShapes(canvas: Canvas) {
        val maxBallY = center.y + yPos
        val ballY = maxBallY.coerceAtMost(canvas.height.toFloat() - radius)
        val ballX = (center.x - xPos).coerceIn(bitmapX, bitmapX + scaledBitmap!!.width)
        canvas.drawCircle(
            (canvas.width / 2).toFloat(),
            (canvas.height / 2).toFloat(),
            (canvas.width / 2).toFloat(),
            paintRect!!
        )
        shapePath.reset()
        shapePath.addCircle(
            (canvas.width / 2).toFloat(),
            (canvas.height / 2).toFloat(),
            ((canvas.width - 32f) / 2).toFloat(),
            Path.Direction.CW
        )
        canvas.drawPath(shapePath, paintRect!!)
        canvas.clipPath(shapePath)
        canvas.drawBitmap(scaledBitmap!!, bitmapX, bitmapY, pathPaint)
        canvas.drawBitmap(
            bitmapCenterScale!!,
            ballX - bitmapCenterScale!!.width / 2,
            ballY - bitmapCenterScale!!.height / 2f,
            paintRect
        )
    }

    val axisPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#201A1A1A")
        strokeWidth = 2f
    }
    val criclePaint = Paint().apply {
        color = Color.parseColor("#40000000")
        style = Paint.Style.STROKE
        strokeWidth = 12f
    }

    val NUM_DASHES = 19
    val DASH_PORTION = 0.25f
    val GAP_PORTION = 0.45f
    val intervals = FloatArray(2)
    private fun drawAxes(canvas: Canvas) {
        canvas.drawLine(
            (canvas.width / 2).toFloat(),
            0f,
            (canvas.width / 2).toFloat(),
            canvas.height.toFloat(),
            axisPaint
        )
        canvas.drawLine(
            0f,
            canvas.height.toFloat() / 2,
            canvas.width.toFloat(),
            canvas.height.toFloat() / 2f,
            axisPaint
        )
        val circumference = 2 * Math.PI * radius
        val dashPlusGapSize = (circumference / NUM_DASHES).toFloat()
        intervals[0] = dashPlusGapSize * DASH_PORTION
        intervals[1] = dashPlusGapSize * GAP_PORTION
        val effect = DashPathEffect(intervals, 0f)
        criclePaint.pathEffect = effect
        canvas.drawCircle(canvas.width / 2f, canvas.width / 2f, radius.toFloat(), criclePaint)
    }

    private var maxMove = 0
    private var k = 0f

    private fun update(value: PointF?) {
        xPos = value!!.x
        yPos = value.y
        invalidate()
    }

    private val handler = Handler()
    var roll = 0f
    var pitch = 0f
    val point = PointF(0f, 0f)
    fun updateOrientation(pitch: Float, roll: Float) {
        if (this.pitch.toInt() != pitch.toInt() || this.roll.toInt() != roll.toInt()) {
            this.pitch = pitch
            this.roll = roll
            point[width * 0.37f * cos(Math.toRadians((90 - roll).toDouble()))
                .toFloat()] = width * 0.37f * cos(Math.toRadians((90 - pitch).toDouble()))
                .toFloat()
            update(point)
//            invalidate()
//            handler.postDelayed({
//                update(point)
//                invalidate()
//            }, 400)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawShapes(canvas)
        drawAxes(canvas)
    }
}
