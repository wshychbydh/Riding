package cool.eye.ridding.db

import android.content.Context
import android.widget.Toast
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cool.eye.ridding.data.Address
import cool.eye.ridding.ui.IBmob
import cool.eye.ridding.ui.dialog.CustomProgressDialog

/**
 * Created by cool on 17-1-19.
 */
class BmobHelper : IBmob {

    var progressDialog: CustomProgressDialog
    var context: Context

    constructor(context: Context) {
        progressDialog = CustomProgressDialog.createDialog(context)
        progressDialog.setCanceledOnTouchOutside(false)
        this.context = context
    }

    override fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun startProgressDialog() {
        progressDialog.show()
    }

    override fun stopProgressDialog() {
        progressDialog.cancel()
    }

    fun saveAddress(address: Address) {
        address.save(object: SaveListener<String?>() {
            override fun done(p0: String?, p1: BmobException?) {
            }
        })
    }
}