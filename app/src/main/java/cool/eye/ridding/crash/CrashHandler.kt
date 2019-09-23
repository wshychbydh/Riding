package cool.eye.ridding.crash

import android.os.Process
import android.util.Log
import cool.eye.ridding.BuildConfig
import cool.eye.ridding.zone.helper.LocalStorage
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

class CrashHandler : Thread.UncaughtExceptionHandler {
    private var mDefaultExceptionHandler: Thread.UncaughtExceptionHandler? = null

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        saveToSDCard(ex)
        if (mDefaultExceptionHandler != null) {
            if (DEBUG) {
                Log.d(TAG, "交给系统去结束我们的程序")
            }
            mDefaultExceptionHandler!!.uncaughtException(thread, ex)
        } else {
            if (DEBUG) {
                Log.d(TAG, "自己结束程序")
            }
            Process.killProcess(Process.myPid())
        }
    }

    private fun saveToSDCard(ex: Throwable) {

        var writer: BufferedWriter? = null
        try {
            writer = BufferedWriter(FileWriter(LocalStorage.createCrashFile(), true))
            var crash = Crash()
            crash.content = Log.getStackTraceString(ex)
            writer.write(crash.toJson())
//
//            //用户信息
//            val currentUser = BmobUser.getCurrentUser(UserModel::class.java)
//            if (currentUser != null) {
//                writer.write("UserId： ${currentUser.objectId}")
//            }
//
//            // 版本
//            writer.write("Version: " + BuildConfig.VERSION_NAME)
//            writer.newLine()
//            writer.write("Build: " + BuildConfig.VERSION_CODE)
//            writer.newLine()
//
//            // 硬件
//            writer.write("Android: " + Build.VERSION.RELEASE)
//            writer.newLine()
//            writer.write("CPU ABI: " + Build.CPU_ABI)
//            writer.newLine()
//            writer.write("Vendor: " + Build.MANUFACTURER)
//            writer.newLine()
//            writer.write("MODEL: " + Build.MODEL)
//            writer.newLine()
//
//            // 时间
//            val format = SimpleDateFormat(PATTERN, Locale.CHINA)
//            writer.write("Date: " + format.format(Date()))
//            writer.newLine()
//
//            // 堆栈信息
//            writer.write(Log.getStackTraceString(ex))
//            writer.newLine()

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (writer != null) {
                try {
                    writer.flush()
                    writer.close()
                } catch (ignore: IOException) {
                }

            }
        }
    }

    companion object {

        private val TAG = CrashHandler::class.java.simpleName
        private val DEBUG = BuildConfig.DEBUG
        private val PATTERN = "yyyy-MM-dd HH:mm:ss.SSS"

        private val sInstance = CrashHandler()

        fun init() {
            sInstance.mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(sInstance)
        }
    }
}
