package cool.eye.ridding.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by cool on 17-1-13.
 */
object Utils {
    fun formatSimpleTime(time: Long): String {
        val format = SimpleDateFormat("HH:mm")
        return format.format(Date(time))
    }

    fun formatFullTime(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return format.format(Date(time))
    }

    fun getTime(time: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return format.parse(time).time
    }

    fun callPhone(context: Context, phone: String) {
        //调起拨打电话，需要用户手动点击
        val intent = Intent(Intent.ACTION_CALL)
        //调起拨打电话，并直接拨打
        //   val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phone")
        context.startActivity(intent)
    }

    fun getStartTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time.time
    }

    fun getEndTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time.time
    }
}