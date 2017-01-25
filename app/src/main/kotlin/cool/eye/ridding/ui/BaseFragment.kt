package cool.eye.ridding.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import cool.eye.ridding.ui.dialog.CustomProgressDialog

/**
 * Created by cool on 17-1-12.
 */

open class BaseFragment : Fragment(), IBmob {

    var progressDialog: CustomProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun startProgressDialog() {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(context)
            progressDialog!!.setCanceledOnTouchOutside(false)
        }
        progressDialog!!.show()
    }

    override fun stopProgressDialog() {
        progressDialog?.cancel()
    }
}
