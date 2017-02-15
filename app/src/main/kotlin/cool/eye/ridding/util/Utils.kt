package cool.eye.ridding.util

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Vibrator
import android.telephony.TelephonyManager
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
            if (c[i].toInt() > 65280 && c[i].toInt() < 65375)
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

    /**
     * 调用系统打电话功能
     */
    fun callPhone(ctx: Context, phoneNum: String, check: Boolean) {
        if (isPhoneNumberValid(phoneNum)) {
            ctx.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum)))
        } else {
            //Util.showToast(ctx, ctx.getString(R.string.phone_format));
        }
    }

    /**
     * 调用系统打电话功能
     */
    fun callPhone(ctx: Context, verify: Boolean?, phoneNum: String) {
        if (verify!!) {
            callPhone(ctx, phoneNum)
        } else {
            ctx.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum)))
        }
    }

    /**
     * 调用系统发短信功能
     */
    fun sendMsg(ctx: Context, phoneNum: String) {
        if (isPhoneNumberValid(phoneNum)) {
            ctx.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNum)))
        } else {
            // Util.showToast(ctx, ctx.getString(R.string.phone_format))
        }
    }

    @JvmStatic fun isPhoneNumber(phoneNumber: CharSequence): Boolean {
        val p = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$")
        val m = p.matcher(phoneNumber)
        return m.matches()
    }

    /**
     * 电话格式 验证，手机/固话 均可
     */
    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        //        if (Util.isEmpty(phoneNumber)) {
        //            return false
        //        }
        var isValid = false
        // String expression = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$"
        // String expression =
        // "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))"
        val expression = "((^(147|170)[0-9]{8}$|(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))"
        val pattern = Pattern.compile(expression)
        val matcher = pattern.matcher(phoneNumber)
        if (matcher.matches()) {
            isValid = true
        }
        return isValid
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
        if (nis != null) {
            nis.indices
                    .filter { nis[it].state == NetworkInfo.State.CONNECTED }
                    .forEach { return true }
        }

        return false
    }

    fun isWifiAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val mWifiState = wifiManager.wifiState
        return mWifiState == WifiManager.WIFI_STATE_ENABLED
    }


    fun showToast(context: Context?, msg: CharSequence?) {
        if (context !is Activity || context == null || msg == null) {
            return
        }
        context.runOnUiThread {
            // Toast toast = Toast.makeText(context, msg,
            // Toast.LENGTH_SHORT)
            // toast.setGravity(Gravity.CENTER, 0, 0)
            val v = LayoutInflater.from(context).inflate(R.layout.toast, null)
            val toast = Toast(context)
            (v.findViewById(R.id.toast_tv) as TextView).text = msg
            toast.view = v
            toast.duration = Toast.LENGTH_SHORT
            toast.show()
        }
    }

    /**
     * 获取手机IMEI

     * @param context
     * *
     * @return
     */
    fun getIMEI(context: Context?): String? {
        if (context == null) {
            return null
        }
        val telephonyManager = context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.deviceId
    }

    /**
     * 震动服务

     * @param context
     * *
     * @param time
     */
    fun vibrate(context: Context?, time: Long) {
        if (context == null) {
            return
        }
        val vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        if (time >= 1000) {
            vibrator.vibrate(time)
        } else {
            // long[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
            vibrator.vibrate(longArrayOf(1000, 2000, 1000, 2000), 1)
        }
    }

    /**
     * 获取apk版本信息

     * @param context
     * *
     * @return
     */
    fun getCurrentVersion(context: Context): String {
        try {
            val info = context.packageManager.getPackageInfo(context.packageName,
                    0)
            return info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return "V1.0"
    }


    fun toggleSoftInput(context: Context) {

        val imm = context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 显示或者隐藏输入法
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    // 输入法是否显示着
    fun isSoftInputOpened(context: Context): Boolean {
        val imm = context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.isActive
    }

    fun showSoftInput(editText: EditText) {
        val imm = editText.context.getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    // 隐藏输入法
    fun hideSoftInput(context: Context?, et: EditText?) {
        // View view = ((Activity) context).getWindow().peekDecorView()
        // if (view != null && view.getWindowToken() != null) {
        // InputMethodManager imm = (InputMethodManager) context
        // .getSystemService(Context.INPUT_METHOD_SERVICE)
        // imm.hideSoftInputFromWindow(view.getWindowToken(),
        // InputMethodManager.HIDE_NOT_ALWAYS)
        // }
        if (context != null && et != null) {
            val imm = context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(et.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun sendSMS(context: Context, mobile: String) {
        //  var mobile = "1890000000;1890000001"
        var intent = Intent(Intent.ACTION_VIEW)
        intent.putExtra("address", mobile)
//intent.putExtra("sms_body", "I am joe!")
        intent.type = "vnd.android-dir/mms-sms"
        context.startActivity(intent)
    }
}