package cool.eye.ridding.zone

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import cool.eye.ridding.R

/**
 * Created by cool on 17-1-25.
 */
class PhotoDialog(var activity: PhotoActivity) : Dialog(activity, R.style.alert_dialog) {

    var view = PhotoView(activity)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view.setClickCallback {
            dismiss()
        }
        setContentView(view)
        setParams()
    }

    private fun setParams() {
        val window = window
        window!!.setWindowAnimations(R.style.PopupWindowAnimation)
        val layoutParams = window.attributes
        val dm = context.resources.displayMetrics
        layoutParams.x = 0
        layoutParams.y = dm.heightPixels
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        onWindowAttributesChanged(layoutParams)
    }
}