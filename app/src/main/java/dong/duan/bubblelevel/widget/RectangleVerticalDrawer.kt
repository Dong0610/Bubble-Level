package dong.duan.bubblelevel.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import android.util.TypedValue
import androidx.core.content.ContextCompat
import dong.duan.bubblelevel.R


class RectangleVerticalDrawer(context: Context, private val isSimple: Boolean) : ViewDrawer<PointF?> {
    private val pathPaint: Paint
    private val ballPaint: Paint
    private var path: Path? = null
    private var xPos = 0f
    private var yPos = 0f
    private val center: Point
    private var radius = 0
    override fun layout(width: Int, height: Int) {
        radius = width / 8
        center[width / 2] = height / 2
        if (path == null) {
            path = Path()
            if (!isSimple) {
                path!!.addRect(0f,0f,width.toFloat(),height.toFloat(), Path.Direction.CCW)
            }
        }
    }
    override fun draw(canvas: Canvas?) {
        //Draw grid
        canvas!!.drawPath(path!!, pathPaint)
        //Draw ball

        var x=(center.x - xPos)
        if (x>canvas.width){
            x= canvas.width -8f
        }
        if (x<0){
            x= 8f
        }
        var y=center.y + yPos
        if(y>canvas.height){
            y= canvas.height-8f
        }
        if(y<0){
            y= 8f
        }
        canvas!!.drawCircle(x, y, 20f, ballPaint)
    }
    override fun update(value: PointF?) {
        xPos = value!!.x
        yPos = value.y
    }
    init {
        val gridColor: Int
        val ballColor: Int
        val typedValue = TypedValue()
        val theme = context.theme
        gridColor = if (theme.resolveAttribute(R.attr.accelerometerGridColor, typedValue, true)) {
            typedValue.data
        } else {
            ContextCompat.getColor(context, R.color.white)
        }
        ballColor = if (theme.resolveAttribute(R.attr.accelerometerBallColor, typedValue, true)) {
            typedValue.data
        } else {
            ContextCompat.getColor(context, R.color.transparent)
        }
        pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pathPaint.strokeWidth = 1f
        pathPaint.style = Paint.Style.STROKE
        pathPaint.color = gridColor
        ballPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        ballPaint.style = Paint.Style.FILL
        ballPaint.color = ballColor
        ballPaint.alpha = Color.alpha(R.color.view_color)
        center = Point(0, 0)
    }
}