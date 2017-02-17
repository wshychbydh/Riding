package cool.eye.ridding.zone.helper

import android.content.Context
import android.os.Environment
import android.os.Looper
import java.io.File

/**
 * @author ycb
 * *
 * @version 1.0
 * *
 * @date 2014-9-20
 * *
 * @category 文件夹构建类
 */
object LocalStorage {

    /**
     * 文件夹根目录
     */

    const val CARD = "card" // 大图
    const val QR = "qr" // 二维码
    const val PHOTO = "photo" // 拍照
    const val LOCAL_DIR = "riding"

    const val CRASH = "crash"
    const val CARD_PRE = "card_"
    const val QR_PRE = "qr_"
    const val PHOTO_PRE = "photo_"
    const val IMAGE_SUFF = ".jpg"
    const val CRASH_PRE = "crash_"
    const val CRASH_SUFF = ".crash"

    // create file storage dir
    @JvmStatic fun composeCrashFileDir(): StringBuilder {
        val sb = StringBuilder()
        sb.append(android.os.Environment.getExternalStorageDirectory())
        sb.append(File.separator)
        sb.append(LOCAL_DIR)
        sb.append(File.separator)
        sb.append(CRASH)
        return sb
    }

    // create file storage path
    fun composeCrashFile(name: String): String {
        val sb = composeCrashFileDir()
        sb.append(File.separator).append(name)
        return sb.toString()
    }

    // create image storage path
    fun composeCardImage(name: String): String {
        val sb = composeCardImageDir()
        sb.append(File.separator).append(name)
        return sb.toString()
    }

    // create image storage dir
    fun composeCardImageDir(): StringBuilder {
        val sb = StringBuilder()
        sb.append(android.os.Environment.getExternalStorageDirectory())
        sb.append(File.separator)
        sb.append(LOCAL_DIR)
        sb.append(File.separator)
        sb.append(CARD)
        return sb
    }

    // create image storage path
    fun composeQrImage(name: String): String {
        val sb = composeQrImageDir()
        sb.append(File.separator).append(name)
        return sb.toString()
    }

    // create image storage dir
    fun composeQrImageDir(): StringBuilder {
        val sb = StringBuilder()
        sb.append(android.os.Environment.getExternalStorageDirectory())
        sb.append(File.separator)
        sb.append(LOCAL_DIR)
        sb.append(File.separator)
        sb.append(QR)
        return sb
    }

    // create image storage path
    fun composePhotoImage(name: String): String {
        val sb = composePhotoImageDir()
        sb.append(File.separator).append(name)
        return sb.toString()
    }

    // create image storage dir
    fun composePhotoImageDir(): StringBuilder {
        val sb = StringBuilder()
        sb.append(android.os.Environment.getExternalStorageDirectory())
        sb.append(File.separator)
        sb.append(LOCAL_DIR)
        sb.append(File.separator)
        sb.append(PHOTO)
        return sb
    }

    //文件存储根目录
    fun getFileRoot(context: Context): String {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val external = context.getExternalFilesDir(null)
            if (external != null) {
                return external.absolutePath
            }
        }
        return context.filesDir.absolutePath
    }

    fun composeQrCodeFile(): String {
        var dir = File(composeQrImageDir().toString())
        if (!dir.exists()) dir.mkdirs()
        return composeQrImage("$QR_PRE${System.currentTimeMillis()}$IMAGE_SUFF")
    }

    fun composeCardFile(): String {
        var dir = File(composeCardImageDir().toString())
        if (!dir.exists()) dir.mkdirs()
        return composeCardImage("$CARD_PRE${System.currentTimeMillis()}$IMAGE_SUFF")
    }

    @JvmStatic fun composePhotoImageFile(): String {
        var dir = File(composePhotoImageDir().toString())
        if (!dir.exists()) dir.mkdirs()
        return composePhotoImage("$PHOTO_PRE${System.currentTimeMillis()}$IMAGE_SUFF")
    }

    @JvmStatic fun createCrashFile(): File {
        var dir = File(composeCrashFileDir().toString())
        if (!dir.exists()) dir.mkdirs()
        var name: String
        if (Looper.myLooper() == Looper.getMainLooper()) {
            name = "MainThread_${System.currentTimeMillis()}$CRASH_SUFF"
        } else {
            name = "ChildThread_${System.currentTimeMillis()}$CRASH_SUFF"
        }
        return File(composeCrashFile(name))
    }
}
