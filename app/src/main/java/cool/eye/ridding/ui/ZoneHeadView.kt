package cool.eye.ridding.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import cool.eye.ridding.R

/**
 * Created by cool on 16-7-7.
 */
class ZoneHeadView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    /**
     * 以下常量是在密度为1的情况下的值。当密度不为1时，需要在此基础上变化。
     */
    private var viewWidth = 90f  //整个View的宽
    private var viewHeight = 90f  //整个View的高
    private var radiusOuter = 45f  //外圆大小
    private var iconSize = 80f // 圆角图片大小
    private var iconLeft = 5f  //图片左边开始位置
    private var iconTop = 5f  //图片顶部开始位置
    private var isFillet = true //传递的图片是否时圆角的，默认为true

    private var bitmap: Bitmap? = null

    private val paint: Paint

    init {
        initSize()
        paint = Paint()
        paint.isAntiAlias = true
        paint.isDither = true
        paint.color = COLOR_OUTER
        paint.style = Paint.Style.FILL_AND_STROKE
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZoneHeadView)
            val count = typedArray.indexCount
            var drawable: Drawable? = null
            (0 until count)
                    .map { typedArray.getIndex(it) }
                    .forEach {
                        if (it == R.styleable.ZoneHeadView_src) {
                            drawable = typedArray.getDrawable(it)
                        } else if (it == R.styleable.ZoneHeadView_isFillet) {
                            isFillet = typedArray.getBoolean(0, true)
                        }
                    }
            if (drawable != null) {
                updateBitmap(drawable!!)
            }
            typedArray.recycle()
        }
    }

    private fun initSize() {
        val density = resources.displayMetrics.density
        viewWidth *= density
        viewHeight *= density
        radiusOuter *= density
        iconSize *= density
        iconLeft *= density
        iconTop *= density
    }

    fun setDrawable(drawable: Drawable, isCircleDrawable: Boolean) {
        this.isFillet = isCircleDrawable
        updateBitmap(drawable)
        invalidate()
    }

    fun setBitmap(bitmap: Bitmap, isFillet: Boolean) {
        this.bitmap = bitmap
        this.isFillet = isFillet
        invalidate()
    }

    private fun updateBitmap(drawable: Drawable) {
        bitmap = createFixedSizeImage((drawable as BitmapDrawable).bitmap)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(viewWidth.toInt(), viewHeight.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap != null) {
            canvas.drawCircle(radiusOuter, radiusOuter, radiusOuter, paint)
            canvas.drawBitmap(bitmap, iconLeft, iconTop, Paint())
        }
    }

    /**
     * 创建固定大小的bitmap。如果图片不是圆角，则圆角图片

     * @param source
     * *
     * @return
     */
    private fun createFixedSizeImage(source: Bitmap): Bitmap {
        val paint = Paint()
        paint.isAntiAlias = true
        val target = Bitmap.createBitmap(iconSize.toInt(), iconSize.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(target)
        if (!isFillet) {
            val size = iconSize / 2
            canvas.drawCircle(size, size, size, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }
        canvas.drawBitmap(scaleBitmap(source), 0f, 0f, paint)
        return target
    }

    /**
     * 缩放bitmap

     * @param bitmap
     * *
     * @return
     */
    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        val minSize = Math.min(bitmap.width, bitmap.height)
        val scale = iconSize / minSize
        matrix.postScale(scale, scale) //长和宽放大缩小的比例
        val resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return resizeBmp
    }

    companion object {
        private val COLOR_OUTER = Color.argb(102, 247, 247, 247)
    }
}
