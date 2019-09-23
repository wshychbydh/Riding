package cool.eye.ridding.zone.card.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.AsyncTask
import cool.eye.ridding.R
import cool.eye.ridding.zone.helper.LocalStorage
import cooleye.scan.encode.QRCodeUtil
import java.io.File
import java.io.FileOutputStream

/**
 * Created by cool on 17-2-8.
 */
object CardHelper {
    var imagePath: String? = null

    fun toImage(context: Context, content: String, paint: Paint, textColor: Int, bgColor: Int, resultBitmap: (bitmap: Bitmap?) -> Unit) {
        object : AsyncTask<String, Void, Bitmap>() {
            override fun doInBackground(vararg params: String): Bitmap? {
                val list = content.split("\n")

                var minWidth = 0f
                list.forEach { content ->
                    var width = paint.measureText(content)
                    minWidth = if (width > minWidth) width else minWidth
                }
                minWidth += 40

                // 计算每一个坐标
                val baseX = 20f
                val baseY = 100f
                // FontMetrics对象
                val fontMetrics = paint.fontMetrics
                val topY = baseY + fontMetrics.top
                val ascentY = baseY + fontMetrics.ascent
                val descentY = baseY + fontMetrics.descent
                val bottomY = baseY + fontMetrics.bottom
                val leading = baseY + fontMetrics.leading

                var height = bottomY - topY
                var maxHeight = list.size * height + height / 2

                val bitmap = Bitmap.createBitmap(minWidth.toInt(), maxHeight.toInt(), Bitmap.Config.ARGB_8888)

                val canvas = Canvas(bitmap)
                paint.color = bgColor
                canvas.drawRect(0f, 0f, minWidth, maxHeight, paint)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
                var startY = height
                paint.color = textColor
                list.forEach { content ->
                    // 绘制文本
                    canvas.drawText(content, baseX, startY, paint)
                    startY += height
                }
                imagePath = LocalStorage.composeCardFile()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(imagePath))
                return bitmap
            }

            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)
                resultBitmap.invoke(result)
            }
        }.execute()
    }

    fun toQrCode(activity: Activity, content: String, icon: Bitmap?, resultBitmap: (bitmap: Bitmap?) -> Unit) {

        //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        object : AsyncTask<String, Void, Bitmap>() {
            override fun doInBackground(vararg params: String): Bitmap? {
                imagePath = LocalStorage.composeQrCodeFile()
                return QRCodeUtil.createQRImage(content, 800, 800, icon, imagePath)
            }

            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)
                resultBitmap.invoke(result)
            }
        }.execute()
    }


    /**
     * 分享功能

     * @param context
     * *            上下文
     * *
     * @param activityTitle
     * *            Activity的名字
     * *
     * @param msgTitle
     * *            消息标题
     * *
     * @param msgText
     * *            消息内容
     * *
     * @param imgPath
     * *            图片路径，不分享图片则传null
     */
    fun shareMsg(context: Context, msgText: String) {
        val intent = Intent(Intent.ACTION_SEND)
        if (imagePath.isNullOrEmpty()) {
            intent.type = "text/plain" // 纯文本
        } else {
            val file = File(imagePath)
            if (file?.exists() && file.isFile) {
                intent.type = "image/jpg"
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.pick_share_method))
        intent.putExtra(Intent.EXTRA_TEXT, msgText)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.card_image)))
    }

    fun shareImage(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/jpg"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.pick_share_method))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.card_image)))
    }
}