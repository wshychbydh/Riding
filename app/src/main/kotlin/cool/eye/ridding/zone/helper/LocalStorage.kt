package cool.eye.ridding.zone.helper

import android.content.Context
import android.os.Environment
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
    const val PHOTO = "photo" // 二维码
    const val LOCAL_DIR = "riding"

    const val FILE = "file"
    val CARD_PRE = "card_"
    val QR_PRE = "qr_"
    val PHOTO_PRE = "photo_"
    val IMAGE_SUFF = ".jpg"

    // create file storage dir
    fun composeFileDir(): String {
        val sb = StringBuilder()
        sb.append(android.os.Environment.getExternalStorageDirectory())
        sb.append(File.separator)
        sb.append(LOCAL_DIR)
        sb.append(File.separator)
        sb.append(FILE)
        return sb.toString()
    }

    // create image storage path
    fun composeCardImage(name: String): String {
        val sb = composeCardImageDir()
        sb.append(File.separator).append(name).append(IMAGE_SUFF)
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
        sb.append(File.separator).append(name).append(IMAGE_SUFF)
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
        sb.append(File.separator).append(name).append(IMAGE_SUFF)
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
        return composeCardImage("$PHOTO_PRE${System.currentTimeMillis()}$IMAGE_SUFF")
    }
}
