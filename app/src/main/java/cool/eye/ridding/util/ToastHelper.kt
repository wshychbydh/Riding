package cool.eye.ridding.util

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast

/**
 *Created by cool on 2018/5/2
 */
object ToastHelper {

    private var lastShowTime = 0L

    @JvmStatic
    fun showToast(context: Context, @StringRes resId: Int) {
        showToast(context, context.getString(resId))
    }

    @JvmStatic
    fun showError(context: Context, throwable: Throwable?) {
        if (throwable?.message?.isNotEmpty() == true) {
            showToast(context, throwable.message!!)
        }
    }

    @JvmStatic
    fun showToast(context: Context, msg: String) {
        if (msg.isEmpty()) return
        if (System.currentTimeMillis() - lastShowTime < 300L) return
        lastShowTime = System.currentTimeMillis()
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}