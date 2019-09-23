package cool.eye.ridding.zone.photo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import cool.eye.ridding.R
import kotlinx.android.synthetic.main.upload_progress.view.*

/**
 * Created by cool on 17-2-14.
 */
class UploadProgressDialog(context: Context) : Dialog(context, R.style.alert_dialog) {
    lateinit var tv_progress: AppCompatTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var view = LayoutInflater.from(context).inflate(R.layout.upload_progress, null)
        tv_progress = view.tv_upload_progress
        setContentView(view)
    }

    fun updateProgress(progress: Int) {
        tv_progress.text = "已上传$progress%"
    }
}