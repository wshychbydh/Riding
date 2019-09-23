package cool.eye.ridding.login.support

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import cool.eye.ridding.R
import java.util.regex.Pattern

/**
 * Created by cool on 15-6-16.
 *
 *
 * 控制密码可见性
 *
 *
 * FIXME:1、显示/隐藏密码时输入法总是会弹出的bug；2、当密码为中文切可见时，第一次点击隐藏不起作用。
 */
class PasswordEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = android.R.attr.editTextStyle) : EditText(context, attrs, defStyle), TextWatcher {

    private var mGravity = Gravity.CENTER_VERTICAL
    private var mTop = 0
    private var mLeft = 0
    private var mWidth = 0
    private var mHeight = 0
    private var mPadding = 0

    private var mShowBitmap: Bitmap? = null
    private var mHideBitmap: Bitmap? = null

    private var mIconTouched = false //显示/隐藏的图片是否正处于点击中

    private var mNeedInvalidate = true //是否需要刷新界面
    private var mIsClipPadding = false
    private var mIsDivision = true  //是否分割文本和图标
    private var mIsConfigChanged = false

    private var mPasswordVisible = false

    private val mShowMethod: TransformationMethod
    private val mHideMethod: TransformationMethod

    init {
        mPasswordVisible = isVisiblePasswordInputType(inputType)
        mShowMethod = HideReturnsTransformationMethod.getInstance()
        mHideMethod = PasswordTransformationMethod.getInstance()
        transformationMethod = if (mPasswordVisible) mShowMethod else mHideMethod
        addTextChangedListener(this)
        if (attrs != null) {
            initDrawInfo(attrs, defStyle)
        }
    }

    private fun initDrawInfo(attrs: AttributeSet, defStyle: Int) {
        val theme = context.theme
        val typedArray = theme.obtainStyledAttributes(attrs, R.styleable.PasswordEditText,
                defStyle, 0)
        val count = typedArray.indexCount
        for (i in 0..count - 1) {
            val attr = typedArray.getIndex(i)
            if (attr == R.styleable.PasswordEditText_padding) {
                setPadding(typedArray.getDimension(attr, 0f).toInt())
            } else if (attr == R.styleable.PasswordEditText_showDrawable) {
                val showDrawable = typedArray.getDrawable(attr)
                mShowBitmap = if (showDrawable == null)
                    null
                else
                    (showDrawable as BitmapDrawable)
                            .bitmap
            } else if (attr == R.styleable.PasswordEditText_hideDrawable) {
                val hideDrawable = typedArray.getDrawable(attr)
                mHideBitmap = if (hideDrawable == null)
                    null
                else
                    (hideDrawable as BitmapDrawable)
                            .bitmap
            } else if (attr == R.styleable.PasswordEditText_gravity) {
                setDrawableGravity(typedArray.getInt(attr, -1))
            } else if (attr == R.styleable.PasswordEditText_clipParentPadding) {
                mIsClipPadding = typedArray.getBoolean(attr, false)
            } else if (attr == R.styleable.PasswordEditText_division) {
                setDivision(typedArray.getBoolean(attr, true))
            }
        }
        typedArray.recycle()
        build()
    }

    private fun build() {
        if (bitmapAvailable()) {
            buildBitmap()
            setupLeftTopOffset()
        }
    }

    private fun buildBitmap() {
        val maxWidth = if (mShowBitmap!!.width > mHideBitmap!!.width)
            mShowBitmap!!.width
        else
            mHideBitmap!!.width
        val maxHeight = if (mShowBitmap!!.height > mHideBitmap!!.height)
            mShowBitmap!!.height
        else
            mHideBitmap!!.height
        mWidth = if (maxWidth > DRAWABLE_MAX_WIDTH)
            DRAWABLE_MAX_WIDTH
        else
            maxWidth
        mHeight = if (maxHeight > DRAWABLE_MAX_HEIGHT)
            DRAWABLE_MAX_HEIGHT
        else
            maxHeight
        mShowBitmap = Bitmap.createScaledBitmap(mShowBitmap, mWidth, mHeight, true)
        mHideBitmap = Bitmap.createScaledBitmap(mHideBitmap, mWidth, mHeight, true)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mIsConfigChanged = true
    }


    fun setDrawable(showDrawable: Drawable, hideDrawable: Drawable): PasswordEditText {
        mShowBitmap = (showDrawable as BitmapDrawable).bitmap
        mHideBitmap = (hideDrawable as BitmapDrawable).bitmap
        buildBitmap()
        return this
    }

    fun setDivision(isDivision: Boolean): PasswordEditText {
        mIsDivision = isDivision
        mNeedInvalidate = true
        return this
    }

    fun setPadding(padding: Int): PasswordEditText {
        mPadding = padding
        mNeedInvalidate = true
        return this
    }

    fun setDrawableGravity(gravity: Int): PasswordEditText {
        if (gravity != -1) {
            if (gravity != mGravity) {
                mGravity = gravity
                mNeedInvalidate = true
            }
        }
        return this
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmapAvailable()) {
            if (mNeedInvalidate || mIsConfigChanged) {
                mNeedInvalidate = false
                setupLeftTopOffset()
            }
            canvas.drawBitmap(if (mPasswordVisible) mShowBitmap else mHideBitmap,
                    (mLeft + scrollX).toFloat(), (mTop + scrollY).toFloat(), paint)
        }
    }

    private fun bitmapAvailable(): Boolean {
        return mShowBitmap != null && mHideBitmap != null
    }

    private fun setupLeftTopOffset() {
        val verticalGravity = mGravity and Gravity.VERTICAL_GRAVITY_MASK
        var topPadding = paddingTop
        var bottomPadding = paddingBottom
        var leftPadding = paddingLeft
        var rightPadding = paddingRight
        when (verticalGravity) {
            Gravity.TOP -> {
                topPadding += mPadding + mHeight - 15
                mTop = (if (mIsClipPadding) 0 else paddingTop) + mPadding
            }
            Gravity.BOTTOM -> {
                bottomPadding += mPadding + mHeight - 15
                mTop = bottom - mHeight - (if (mIsClipPadding) 0 else paddingBottom) -
                        mPadding - top
            }
            Gravity.CENTER_VERTICAL -> mTop = (bottom - top - mHeight) / 2
            else -> mTop = mPadding
        }
        val horizontalGravity = mGravity and Gravity.HORIZONTAL_GRAVITY_MASK
        when (horizontalGravity) {
            Gravity.LEFT -> {
                if (!mIsConfigChanged)
                    leftPadding += mPadding + mWidth
                mLeft = (if (mIsClipPadding) 0 else paddingLeft) + mPadding
            }
            Gravity.RIGHT -> {
                if (!mIsConfigChanged)
                    rightPadding += mPadding + mWidth
                mLeft = right - mWidth - (if (mIsClipPadding) 0 else paddingRight) - mPadding / 2 - left
            }
            Gravity.CENTER_HORIZONTAL -> mLeft = (right - left - mWidth) / 2
            else -> mLeft = mPadding
        }
        if (mIsDivision)
            setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
        mIsConfigChanged = false
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(ss: CharSequence, start: Int, before: Int, count: Int) {
        val editable = text.toString()
        val str = passwordFilter(editable)
        if (editable != str) {
            setText(str)
            //设置新的光标所在位置
            setSelection(str.length)
        }
    }

    override fun afterTextChanged(s: Editable) {
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (bitmapAvailable()) {
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> if (isPointInRect(event.x, event.y)) {
                    mIconTouched = true
                }
                MotionEvent.ACTION_CANCEL -> mIconTouched = false
                MotionEvent.ACTION_UP -> {
                    if (mIconTouched && isPointInRect(event.x, event.y)) {
                        mPasswordVisible = !mPasswordVisible
                        transformationMethod = if (mPasswordVisible) mShowMethod else mHideMethod
                    }
                    mIconTouched = false
                }
                else -> {
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun isPointInRect(x: Float, y: Float): Boolean {
        return x > mLeft && x < mLeft + mWidth && y > mTop && y < mTop + mHeight
    }

    private fun isVisiblePasswordInputType(inputType: Int): Boolean {
        val variation = inputType and (EditorInfo.TYPE_MASK_CLASS or EditorInfo.TYPE_MASK_VARIATION)
        return variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    }

    private fun passwordFilter(password: String): String {
        // 只允许字母、数字和汉字
        val regEx = "[^a-zA-Z0-9._]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(password)
        return m.replaceAll("").trim { it <= ' ' }
    }

    companion object {

        private val DRAWABLE_MAX_WIDTH = 80
        private val DRAWABLE_MAX_HEIGHT = 80
    }
}
