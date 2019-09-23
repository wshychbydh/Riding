package cool.eye.ridding.zone.card.ui

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import cool.eye.ridding.R
import cool.eye.ridding.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_scan_result.*
import kotlinx.android.synthetic.main.common_title.*

class ScanResultActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)
        iv_back.setOnClickListener { finish() }
        tv_title.text = "二维码内容"
        tv_scan_content.text = intent.getStringExtra(CONTENT)
        iv_scan.setImageBitmap(intent.getParcelableExtra(BITMAP))
        btn_scan_copy.setOnClickListener {
            // 从API11开始android推荐使用android.content.ClipboardManager
            // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = intent.getStringExtra(CONTENT)
            toast("复制成功")
        }
    }

    companion object {
        const val CONTENT = "content"
        const val BITMAP = "bitmap"
        @JvmStatic fun launch(activity: CaptureActivity, bitmap: Bitmap, content: String) {
            var intent = Intent(activity, ScanResultActivity::class.java)
            intent.putExtra(BITMAP, bitmap)
            intent.putExtra(CONTENT, content)
            activity.startActivity(intent)
        }
    }
}
