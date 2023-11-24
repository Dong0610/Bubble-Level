package dong.duan.bubblelevel.widget

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF

object AndroidUtils {
    fun dpToPx(dp: Int): Float {
        return dpToPx(dp.toFloat())
    }
    private fun dpToPx(dp: Float): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }

}

fun Canvas.drawTriangle(
    borderPaint: Paint,
    firstPoint: PointF,
    secondPoint: PointF,
    threePoint: PointF
) {
    val shapePath = Path()
    shapePath.moveTo(firstPoint.x, firstPoint.y)
    shapePath.lineTo(secondPoint.x, secondPoint.y)
    shapePath.lineTo(threePoint.x, threePoint.y)
    shapePath.close()
    drawPath(shapePath, borderPaint)
}


fun Canvas.drawRoundRectPath(
    rectF: RectF,
    radius: Float,
    roundTopLeft: Boolean,
    roundTopRight: Boolean,
    roundBottomLeft: Boolean,
    roundBottomRight: Boolean,
    paint: Paint
) {
    val path = Path()
    if (roundBottomLeft) {
        path.moveTo(rectF.left, rectF.bottom - radius)
    } else {
        path.moveTo(rectF.left, rectF.bottom)
    }
    if (roundTopLeft) {
        path.lineTo(rectF.left, rectF.top + radius)
        path.quadTo(rectF.left, rectF.top, rectF.left + radius, rectF.top)
    } else {
        path.lineTo(rectF.left, rectF.top)
    }
    if (roundTopRight) {
        path.lineTo(rectF.right - radius, rectF.top)
        path.quadTo(rectF.right, rectF.top, rectF.right, rectF.top + radius)
    } else {
        path.lineTo(rectF.right, rectF.top)
    }
    if (roundBottomRight) {
        path.lineTo(rectF.right, rectF.bottom - radius)
        path.quadTo(rectF.right, rectF.bottom, rectF.right - radius, rectF.bottom)
    } else {
        path.lineTo(rectF.right, rectF.bottom)
    }
    if (roundBottomLeft) {
        path.lineTo(rectF.left + radius, rectF.bottom)
        path.quadTo(rectF.left, rectF.bottom, rectF.left, rectF.bottom - radius)
    } else {
        path.lineTo(rectF.left, rectF.bottom)
    }
    path.close()

    drawPath(path, paint)
}
