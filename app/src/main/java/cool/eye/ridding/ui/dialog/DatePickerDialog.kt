package cool.eye.ridding.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import cool.eye.ridding.R
import cool.eye.ridding.util.Utils
import kotlinx.android.synthetic.main.dialog_datepicker.view.*
import java.util.*

/**
 * Created by cool on 17-1-13.
 */
class DatePickerDialog : DialogFragment() {

    private lateinit var data_picker: DatePicker
    private lateinit var time_picker: TimePicker

    var currentTime: String? = null
    var onDateSelectedListener: ((time: String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cotnext = requireContext()
        val view = LayoutInflater.from(cotnext).inflate(R.layout.dialog_datepicker, null)

        // Create a new instance of DatePickerDialog and return it
        return AlertDialog.Builder(cotnext, R.style.picker_dialog)
                .setView(view)
                .setNegativeButton(R.string.cancel) { _, _ -> dismiss() }
            .setPositiveButton(R.string.sure) { _, _ ->
                if (onDateSelectedListener != null) onDateSelectedListener!!.invoke(getTime(view))
                dismiss()
            }.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTime()
    }

    private fun setTime() {
        var canclander = Calendar.getInstance()
        canclander.time = if (currentTime.isNullOrEmpty()) Date() else Utils.parseFullTime(currentTime!!)
        data_picker.init(canclander.get(Calendar.YEAR), canclander.get(Calendar.MONTH), canclander.get(Calendar.DAY_OF_MONTH), null)
        time_picker.currentHour = canclander.get(Calendar.HOUR_OF_DAY)
        time_picker.currentMinute = canclander.get(Calendar.MINUTE)
    }

    private fun getTime(view: View): String {
        var year = view.date_picker.year
        var month = view.date_picker.month + 1
        var day = view.date_picker.dayOfMonth
        var hour = view.time_picker.currentHour
        var minute = view.time_picker.currentMinute
        return "$year-${formatNumber(month)}-${formatNumber(day)} ${formatNumber(hour)}:${formatNumber(minute)}"
    }

    private fun formatNumber(num: Int): String {
        return if (num <= 9) {
            "0$num"
        } else {
            "$num"
        }
    }
}