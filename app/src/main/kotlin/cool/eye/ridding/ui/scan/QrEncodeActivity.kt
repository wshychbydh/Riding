package cool.eye.ridding.ui.scan

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import cool.eye.ridding.R
import cool.eye.ridding.zone.PhotoActivity
import cool.eye.ridding.zone.PhotoDialog
import cooleye.scan.encode.QRCodeUtil
import kotlinx.android.synthetic.main.common_title.*
import kotlinx.android.synthetic.main.qr_encode.*
import java.io.File


/**
 * 二维码生成
 */
class QrEncodeActivity : PhotoActivity() {

    var imagePath: String? = null
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_encode)
        bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        tv_title.text = "我的二维码"
        iv_back.setOnClickListener { finish() }
        create_qr_btn.setOnClickListener {
            if (create_qr_content.text.toString().isNullOrEmpty()) {
                Toast.makeText(this, "请输入二维码内容", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val filePath = "${getFileRoot()}${File.separator}qr_${System.currentTimeMillis()}.jpg"

            //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
            Thread(Runnable {
                val success = QRCodeUtil.createQRImage(create_qr_content.text.toString().trim(), 800, 800,
                        if (create_qr_icon.isChecked) bitmap else null,
                        filePath)

                if (success) {
                    imagePath = filePath
                    runOnUiThread { create_qr_iv.setImageBitmap(BitmapFactory.decodeFile(filePath)) }
                }
            }).start()
        }
        create_qr_iv.setOnClickListener {
            if (imagePath != null) {
                shareMsg("选择分享方式", "我的名片", create_qr_content.text.toString(), imagePath)
            }
        }

        icon.setOnClickListener {
            var dialog = PhotoDialog(this)
            setITakePhoto(dialog.view.photoInterface)
            dialog.view.setPhotoCallback { bitmap ->
                this@QrEncodeActivity.bitmap = bitmap
                icon.setImageBitmap(bitmap)
            }
            dialog.show()
        }
    }

    //文件存储根目录
    private fun getFileRoot(): String {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val external = getExternalFilesDir(null)
            if (external != null) {
                return external.absolutePath
            }
        }
        return filesDir.absolutePath
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
    fun shareMsg(activityTitle: String, msgTitle: String, msgText: String,
                 imgPath: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        if (imgPath == null || imgPath == "") {
            intent.type = "text/plain" // 纯文本
        } else {
            val f = File(imgPath)
            if (f != null && f.exists() && f.isFile) {
                intent.type = "image/jpg"
                val u = Uri.fromFile(f)
                intent.putExtra(Intent.EXTRA_STREAM, u)
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle)
        intent.putExtra(Intent.EXTRA_TEXT, msgText)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(Intent.createChooser(intent, activityTitle))
    }
}