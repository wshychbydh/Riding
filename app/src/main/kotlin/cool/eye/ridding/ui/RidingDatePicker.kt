package cool.eye.ridding.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import cool.eye.ridding.R
import kotlinx.android.synthetic.main.dialog_datepicker.view.*
import java.util.*

/**
 * Created by cool on 17-1-13.
 */
class RidingDatePicker(context: Context) : Dialog(context, R.style.alert_dialog) {

    lateinit var data_picker: DatePicker
    lateinit var time_picker: TimePicker

    var currentTime: Long = System.currentTimeMillis()
    var onDateSelectedListener: ((time: String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var view = LayoutInflater.from(context).inflate(R.layout.dialog_datepicker, null)
        setContentView(view)
        data_picker = view.date_picker
        time_picker = view.time_picker
        setParams()
        setTime()
        view.sure.setOnClickListener {
            if (onDateSelectedListener != null) onDateSelectedListener!!.invoke(getTime())
            dismiss()
        }
    }

    private fun setTime() {
        var canclander = Calendar.getInstance()
        canclander.time = Date(currentTime)
        data_picker.init(canclander.get(Calendar.YEAR), canclander.get(Calendar.MONTH), canclander.get(Calendar.DAY_OF_MONTH), null)
        time_picker.currentHour = canclander.get(Calendar.HOUR_OF_DAY)
        time_picker.currentMinute = canclander.get(Calendar.MINUTE)
    }

    private fun setParams() {
        setCanceledOnTouchOutside(false)
        window.setWindowAnimations(R.style.DialogWindowAnimation)
        window.attributes.x = 0
        window.attributes.y = context.resources.displayMetrics.heightPixels
        window.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
        window.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
        onWindowAttributesChanged(window.attributes)
    }

    fun getTime(): String {
        var year = data_picker.year
        var month = data_picker.month + 1
        var day = data_picker.dayOfMonth
        var hour = time_picker.currentHour
        var minute = time_picker.currentMinute
        var monthStr = if (month <= 9) "0$month" else month
        return "$year-$monthStr-$day $hour:$minute"
    }
}