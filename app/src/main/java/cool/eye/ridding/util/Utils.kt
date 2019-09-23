package cool.eye.ridding.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import cool.eye.ridding.R
import java.io.File
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by cool on 17-1-13.
 */
object Utils {

    /**
     * 判断网络是否连接
     *
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`

     * @param context 上下文
     * *
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isNetAvaiable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    fun formatSimpleTime(time: Long): String {
        val format = SimpleDateFormat("HH:mm")
        return format.format(Date(time))
    }

    fun formatSimpleTime(time: String): String {
        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val format2 = SimpleDateFormat("HH:mm")
        return format2.format(Date(format1.parse(time).time))
    }

    fun parseFullTime(time: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return format.parse(time)
    }

    fun getDefaultTime(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return format.format(Date())
    }

    fun callPhone(context: Context, phone: String) {
        //调起拨打电话，并直接拨打
        // val intent = Intent(Intent.ACTION_CALL)
        //调起拨打电话，需要用户手动点击
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phone")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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

    fun getEndTime(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(Calendar.HOUR, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time.time
    }

    /**
     * 批量发送短信
     * @param context
     * @param phones
     * @param message
     */
    fun sendMessage(context: Context, phones: String, message: String) {
        // mobile = "18900000001890000001"
        var intent = Intent(Intent.ACTION_VIEW)
        intent.putExtra("address", phones)
        intent.putExtra("sms_body", message)
        intent.setType("vnd.android-dir/mms-sms")
        context.startActivity(intent)
    }


    fun passwordFilter(password: String): String {
        // 只允许字母和数字
        var regEx = "[^a-zA-Z0-9]"
        var p = Pattern.compile(regEx)
        var m = p.matcher(password)
        return m.replaceAll("").trim()
    }

    fun formatSizeOfStr(cs: CharSequence, size: Int, from: Int, to: Int): SpannableString {
        var span = SpannableString(cs)
        span.setSpan(AbsoluteSizeSpan(size, true), from, to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }

    fun formatColorOfStr(cs: CharSequence, color: Int, from: Int, to: Int): SpannableString {
        val span = SpannableString(cs)
        span.setSpan(ForegroundColorSpan(color), from, to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }

    fun formatPartColorOfStr(color: Int, all: CharSequence, part: Any): SpannableString {
        val span = SpannableString(all)
        var start = all.indexOf(part.toString())
        var len = part.toString().length
        span.setSpan(ForegroundColorSpan(color), start, start + len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号

     * @param str
     * *
     * @return
     */
    fun stringFilter(str: String): String {
        var str = str
        str = str.replace("【".toRegex(), "[").replace("】".toRegex(), "]").replace("！".toRegex(), "!")
                .replace("：".toRegex(), ":")// 替换中文标号
        val regEx = "[『』]" // 清除掉特殊字符
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return m.replaceAll("").trim { it <= ' ' }
    }

    /**
     * 半角转换为全角

     * @param input
     * *
     * @return
     */
    fun toDBC(input: String): String {
        val c = input.toCharArray()
        for (i in c.indices) {
            if (c[i].toInt() == 12288) {
                c[i] = 32.toChar()
                continue
            }
            if (c[i].toInt() in 65281..65374)
                c[i] = (c[i].toInt() - 65248).toChar()
        }
        return String(c)
    }

    /**
     * 分享

     * @param content 文本
     * *
     * @param path    图片
     */
    fun share(context: Context, title: String, content: String,
              file: File?) {
        val intent = Intent(Intent.ACTION_SEND)
        // 设置Intent的内容类型为image/png.
        intent.type = "image/*"
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(Intent.EXTRA_TITLE, title)
        }
        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(Intent.EXTRA_TEXT, content)
        }
        if (file != null) {
            // 通过创建File的对象获取外存（SDCard）中的图片
            // 获取文件的URL
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(intent, "分享"))
    }

    /**
     * 分享

     * @param content 文本
     */
    fun share(context: Context, content: String) {
        val intent = Intent(Intent.ACTION_SEND)

        intent.type = "text/plain"
        if (!TextUtils.isEmpty(content)) {
            intent.putExtra(Intent.EXTRA_TEXT, content)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(intent,
                context.resources.getString(R.string.app_name)))
    }

    /**
     * 获取状态栏高度

     * @param context
     * *
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        var c: Class<*>? = null
        var obj: Any? = null
        var field: Field? = null
        var x = 0
        var statusBarHeight = 0
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c!!.newInstance()
            field = c.getField("status_bar_height")
            x = Integer.parseInt(field!!.get(obj).toString())
            statusBarHeight = context.resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return statusBarHeight
    }

    fun getActionBarHeight(context: Context): Int {
        val tv = TypedValue()
        if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.resources
                    .displayMetrics)
        }
        return 0
    }

    @JvmStatic fun isPhoneNumber(phoneNumber: CharSequence): Boolean {
        val p = Pattern.compile("^1[3|4|5|7|8|9][0-9]\\d{8}$")
        val m = p.matcher(phoneNumber)
        return m.matches()
    }

    /**
     * 邮箱
     */
    fun isEmail(email: String): Boolean {
        val str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
        val p = Pattern.compile(str)
        val m = p.matcher(email)

        return m.matches()
    }

    /**
     * 检测是否有网络

     * @param context
     * *
     * @return
     */
    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val nis = connm.allNetworkInfo
        nis?.indices?.filter { nis[it].state == NetworkInfo.State.CONNECTED }?.forEach { _ -> return true }

        return false
    }
}