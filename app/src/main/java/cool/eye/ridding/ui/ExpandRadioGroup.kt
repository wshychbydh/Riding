package cool.eye.ridding.ui

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import cool.eye.ridding.R

/**
 * Created by cool on 16-7-13.
 */
class ExpandRadioGroup @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {
    private var lastSelection: Int = 0
    var onCheckedChangeListener: ((selection: Int) -> Unit)? = null

    init {
        var tv: TextView
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.weight = 1f
        val height = (42 * resources.displayMetrics.density).toInt()
        for (index in CONTENTS.indices) {
            tv = TextView(context)
            tv.layoutParams = params
            tv.height = height
            tv.gravity = Gravity.CENTER
            tv.text = CONTENTS[index]
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
            tv.setOnClickListener { v ->
                if (index != lastSelection) {
                    updateSelection(index)
                }
            }
            addView(tv)
        }
    }

    var selection: Int
        get() = lastSelection
        set(selection) = updateSelection(selection)

    private fun updateSelection(selection: Int) {
        lastSelection = selection
        onCheckedChangeListener?.invoke(selection)
        for (index in CONTENTS.indices) {
            val tv = getChildAt(index) as TextView
            if (index == selection) {
                tv.setTextColor(resources.getColor(R.color.colorPrimary))
                tv.setBackgroundResource(R.drawable.table_bg)
            } else {
                tv.setTextColor(resources.getColor(R.color.content))
                tv.setBackgroundColor(resources.getColor(R.color.white))
            }
        }
    }

    companion object {

        private val CONTENTS = arrayOf("待完成", "已完成")
    }
}
