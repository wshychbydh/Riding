package cool.eye.ridding.crash

import android.os.Build
import com.google.gson.Gson
import cool.eye.ridding.BuildConfig
import cool.eye.ridding.data.BaseBmobObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by cool on 17-2-17.
 */
class Crash : BaseBmobObject() {

    var version: String = BuildConfig.VERSION_NAME
    var build_code: Int = BuildConfig.VERSION_CODE
    var build_version: String = Build.VERSION.RELEASE
    var cpu_abi: String = Build.CPU_ABI
    var vendor: String = Build.MANUFACTURER
    var model: String = Build.MODEL
    var thread: String = Thread.currentThread().name
    var date: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA).format(Date())
    var content: String? = null

    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun parseJson(json: String): Crash {
            return Gson().fromJson(json, Crash::class.java)
        }
    }
}