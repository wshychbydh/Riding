package cool.eye.ridding.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.annotation.WorkerThread
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.Toast
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UploadFileListener
import com.eye.cool.photo.utils.ImageUtil
import cool.eye.ridding.zone.photo.UploadProgressDialog
import java.io.File
import java.io.InputStream
import java.net.URL

/**
 *Created by ycb on 2019/9/23 0023
 */
object NetImageUtil {

  fun setCircleBitmapToImageView(iv: ImageView, path: String, size: Float = 65f) {
    ThreadUtil.sync({
      val bitmap = loadImageFromNet(path) ?: return@sync null
      ImageUtil.createCircleImage(bitmap, size)
    }, {
      iv.setImageBitmap(it)
    })
  }

  @WorkerThread
  fun loadImageFromNet(url: String?): Bitmap? {
    if (url.isNullOrEmpty()) return null
    if (!URLUtil.isValidUrl(url)) return null
    var inputStream: InputStream? = null
    try {
      val url = URL(url)
      val conn = url.openConnection()
      conn.doInput = true
      conn.connect()
      inputStream = conn.getInputStream()
      return BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      inputStream?.close()
    }
    return null
  }

  @WorkerThread
  fun uploadImage(context: Context, path: String, onDone: (String) -> Unit) {
    val progress = UploadProgressDialog(context)
    progress.show()
    val bmobFile = BmobFile(File(path))
    bmobFile.uploadblock(object : UploadFileListener() {

      override fun done(e: BmobException?) {
        if (e == null) {
          onDone.invoke(bmobFile.fileUrl)
        } else {
          Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        progress.dismiss()
      }

      override fun onProgress(value: Int?) {
        // 返回的上传进度（百分比）
        progress.updateProgress(value!!)
      }
    })
  }
}