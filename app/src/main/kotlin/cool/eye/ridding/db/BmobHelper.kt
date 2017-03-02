package cool.eye.ridding.db

import android.content.Context
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
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

    /**
     * type:0标识riding,1标识carry
     */
    fun saveAddress(name: String, type: Int) {
        var address = Address()
        address.name = name
        if (type == 0) {
            address.ridingCount = 1
        } else {
            address.carryCount = 1
        }

        var query = BmobQuery<Address>()
        query.addWhereEqualTo("userId", BmobUser.getCurrentUser().objectId)
        query.addWhereEqualTo("name", name)
        query.findObjects(object : FindListener<Address?>() {
            override fun done(p0: MutableList<Address?>?, p1: BmobException?) {
                if (p0 != null && p0.isNotEmpty()) {
                    address.objectId = p0[0]!!.objectId
                    if (type == 0) {
                        address.ridingCount = p0[0]!!.ridingCount + 1
                        address.carryCount = p0[0]!!.carryCount
                    } else {
                        address.carryCount = p0[0]!!.carryCount + 1
                        address.ridingCount = p0[0]!!.ridingCount
                    }
                    address.update(p0[0]!!.objectId, object : UpdateListener() {
                        override fun done(p0: BmobException?) {
                        }
                    })
                } else {
                    address.save(object : SaveListener<String?>() {
                        override fun done(p0: String?, p1: BmobException?) {
                        }
                    })
                }
            }
        })
    }
}