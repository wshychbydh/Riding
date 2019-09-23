package cool.eye.ridding.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import cool.eye.ridding.ui.dialog.CustomProgressDialog

/**
 * Created by cool on 17-1-12.
 */

open class BaseActivity : AppCompatActivity(), IBmob {

    var progressDialog: CustomProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun toActivity(targetActivity: Class<out Activity>) {
        startActivity(Intent(this, targetActivity))
        finish()
    }

    override fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun startProgressDialog() {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this)
            progressDialog!!.setCanceledOnTouchOutside(false)
        }
        progressDialog!!.show()
    }

    override fun stopProgressDialog() {
        progressDialog?.cancel()
    }
}
