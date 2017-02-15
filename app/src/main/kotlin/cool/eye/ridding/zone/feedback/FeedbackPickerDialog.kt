package cn.sudiyi.app.client.account.support

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.TextView
import cool.eye.ridding.R
import kotlinx.android.synthetic.main.feedback_picker.view.*

/**
 * Created by cool on 16-9-22.
 */
class FeedbackPickerDialog(context: Context) : Dialog(context, R.style.alert_dialog) {

    var onSelectionListener: ((type: FeedbackType) -> Unit)? = null

    init {
        setContentView(buildContentView())
        setParams()
    }

    private fun buildContentView(): View {
        val v = LayoutInflater.from(context).inflate(R.layout.feedback_picker, null)
        v.feedback_listView.adapter = FeedbackAdapter()
        v.feedback_listView.dividerHeight = 1
        v.feedback_listView.divider = context.resources.getDrawable(R.drawable.divider_gray)
        v.feedback_cancel.setOnClickListener { dismiss() }
        return v
    }

    private fun setParams() {
        window.setWindowAnimations(R.style.PopupWindowAnimation)
        val layoutParams = window.attributes
        val dm = context.resources.displayMetrics
        layoutParams.x = 0
        layoutParams.y = dm.heightPixels
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        onWindowAttributesChanged(layoutParams)
    }

    private inner class FeedbackAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return FeedbackType.values().size
        }

        override fun getItem(position: Int): Any {
            return FeedbackType.values()[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, contentView: View?, parent: ViewGroup?): View? {
            val type = FeedbackType.values()[position]
            val tv = TextView(context)
            tv.layoutParams = AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                                                       AbsListView.LayoutParams.WRAP_CONTENT)
            tv.minHeight = (44 * context.resources.displayMetrics.density).toInt()
            tv.setBackgroundResource(R.drawable.bg_btn_white  )
            tv.setOnClickListener {
                onSelectionListener?.invoke(type)
                dismiss()
            }
            tv.textSize = 16f
            tv.gravity = Gravity.CENTER
            tv.text = type.value
            return tv
        }
    }
}
