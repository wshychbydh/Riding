package cool.eye.ridding.widget

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.StateSet
import android.widget.ImageView

/**
 * When assigned a normal drawable, this class will add a pressed state automatically.
 * By default_img, a 50% transparent affect is applied to the pressed state. And you can customized
 * it by adding a PorterDuffColorFilter.
 */
class StatedImageView : ImageView {

    var pressedColorFilter: PorterDuffColorFilter? = null
        get() = if (field != null) field else DEFAULT_COLOR_FILTER

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (!drawable!!.isStateful) {
            val stateDrawable = ButtonStateListDrawable()
            stateDrawable.addState(StateSet.WILD_CARD, drawable)
            super.setImageDrawable(stateDrawable)
        } else {
            super.setImageDrawable(drawable)
        }
    }

    override fun setImageResource(resId: Int) {
        setImageDrawable(resources.getDrawable(resId))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private inner class ButtonStateListDrawable : StateListDrawable() {

        override fun onStateChange(stateSet: IntArray): Boolean {
            val hasPressedState = stateSet.contains(R.attr.state_pressed)
            if (hasPressedState) {
                colorFilter = pressedColorFilter
            } else {
                clearColorFilter()
            }
            return super.onStateChange(stateSet)
        }
    }

    companion object {

        private val DEFAULT_COLOR_FILTER = PorterDuffColorFilter(0x80FFFFFF.toInt(), PorterDuff.Mode.MULTIPLY)
    }
}
