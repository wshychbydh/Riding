package cool.eye.ridding.login.support

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.widget.EditText
import cool.eye.ridding.R

/**
 * Created by lenayan on 14-4-16.
 */
class ClearableEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = android.R.attr.editTextStyle) : EditText(context, attrs, defStyle) {

    private var textCount = 0
    private var clearIconGravity = Gravity.TOP
    private var clearIconTop = 0
    private var clearIconLeft = 0
    private var clearIconWidth = 0
    private var clearIconHeight = 0
    private var clearIconPadding = 0
    private var lastX = -1f
    private var lastY = -1f

    private var clearIconDrawable: Drawable? = null

    private var needInvalidate = true
    private var isClearIconClipPadding = false
    private var isClearIconDevision = true
    private var isConfigChanged = false

    init {
        val theme = context.theme
        val typedArray = theme.obtainStyledAttributes(attrs, R.styleable
                .ClearableEditText, defStyle, 0)
        val count = typedArray.indexCount
        for (i in 0..count - 1) {
            val attr = typedArray.getIndex(i)
            if (attr == R.styleable.ClearableEditText_clearIconPadding) {
                setClearIconPadding(typedArray.getDimension(attr, 0f).toInt())
            } else if (attr == R.styleable.ClearableEditText_clearIconDrawable) {
                val drawable = typedArray.getDrawable(attr)
                if (drawable != null) {
                    clearIconDrawable = drawable
                }
            } else if (attr == R.styleable.ClearableEditText_clearIconGravity) {
                setClearIconGravity(typedArray.getInt(attr, -1))
            } else if (attr == R.styleable.ClearableEditText_clearIconClipParentPadding) {
                isClearIconClipPadding = typedArray.getBoolean(attr, false)
            } else if (attr == R.styleable.ClearableEditText_clearIconDivision) {
                setClearIconDivision(typedArray.getBoolean(attr, true))
            }
        }
        typedArray.recycle()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isConfigChanged = true
    }

    fun setClearIconDivision(isClearIconDivision: Boolean) {
        isClearIconDevision = isClearIconDivision
    }

    fun setClearIconPadding(clearIconPadding: Int) {
        this.clearIconPadding = clearIconPadding
    }

    fun setClearIconGravity(clearIconGravity: Int) {
        if (clearIconGravity == -1)
            return
        if (clearIconGravity != this.clearIconGravity) {
            needInvalidate = true
            this.clearIconGravity = clearIconGravity
            invalidate()
        }
    }

    fun hideClear() {
        textCount = 0
    }

    fun showClear(size: Int) {
        textCount = size
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (clearIconDrawable != null && isEnabled) {
            val bitmap = (clearIconDrawable as BitmapDrawable).bitmap
            clearIconHeight = bitmap.height
            clearIconWidth = bitmap.width
            if (textCount > 0) {
                if (needInvalidate || isConfigChanged) {
                    needInvalidate = false
                    setupLeftTopOffset()
                }
                val left = clearIconLeft + scrollX
                val top = clearIconTop + scrollY
                val paint = paint
                canvas.drawBitmap(bitmap, left.toFloat(), top.toFloat(), paint)
            }
        }
    }

    private fun setupLeftTopOffset() {
        val verticalGravity = clearIconGravity and Gravity.VERTICAL_GRAVITY_MASK
        var topPadding = paddingTop
        var bottomPadding = paddingBottom
        var leftPadding = paddingLeft
        var rightPadding = paddingRight
        when (verticalGravity) {
            Gravity.TOP -> {
                topPadding += clearIconPadding + clearIconHeight - 15
                clearIconTop = (if (isClearIconClipPadding) 0 else paddingTop) + clearIconPadding
            }
            Gravity.BOTTOM -> {
                bottomPadding += clearIconPadding + clearIconHeight - 15
                clearIconTop = bottom - clearIconHeight - (if (isClearIconClipPadding)
                    0
                else
                    paddingBottom) - clearIconPadding - top
            }
            Gravity.CENTER_VERTICAL -> clearIconTop = (bottom - top - clearIconHeight) / 2
            else -> clearIconTop = clearIconPadding
        }
        val horizontalGravity = clearIconGravity and Gravity.HORIZONTAL_GRAVITY_MASK
        when (horizontalGravity) {
            Gravity.LEFT -> {
                if (!isConfigChanged)
                    leftPadding += clearIconPadding + clearIconWidth
                clearIconLeft = (if (isClearIconClipPadding) 0 else paddingLeft) + clearIconPadding
            }
            Gravity.RIGHT -> {
                if (!isConfigChanged)
                    rightPadding += clearIconPadding + clearIconWidth
                clearIconLeft = right - clearIconWidth - (if (isClearIconClipPadding)
                    0
                else
                    paddingRight) - clearIconPadding / 2 - left
            }
            Gravity.CENTER_HORIZONTAL -> clearIconLeft = (right - left - clearIconWidth) / 2
            else -> clearIconLeft = clearIconPadding
        }
        if (isClearIconDevision)
            setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
        isConfigChanged = false
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int,
                               lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        textCount = text?.length ?: 0
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return super.onTouchEvent(event)
        }
        val action = event.action
        val x = event.x
        val y = event.y
        val left = clearIconLeft - clearIconPadding
        val top = clearIconTop - clearIconPadding
        val right = clearIconLeft + clearIconWidth + clearIconPadding
        val bottom = clearIconTop + clearIconHeight + clearIconPadding

        when (action) {
            MotionEvent.ACTION_DOWN -> if (textCount > 0 && clearIconDrawable != null && x > left && x < right && y < bottom && y > top) {
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_CANCEL -> {
            }
            MotionEvent.ACTION_UP -> if (lastX != -1f && lastY != -1f) {
                if (x > left && x < right && y < bottom && y > top) {
                    setText("")
                }
                lastX = -1f
                lastY = -1f
            }
            MotionEvent.ACTION_MOVE -> {
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }
}
