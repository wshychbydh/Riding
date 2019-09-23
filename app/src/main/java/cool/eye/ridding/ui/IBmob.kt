package cool.eye.ridding.ui

import cn.bmob.v3.BmobObject
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import cool.eye.ridding.util.DeleteDataListener
import cool.eye.ridding.util.SaveDataListener
import cool.eye.ridding.util.UpdateDataListener

/**
 * Created by cool on 17-1-19.
 */
interface IBmob {

    fun BmobObject.saveData(listener: SaveDataListener) {
        startProgressDialog()
        save(object : SaveListener<String?>() {
            override fun done(p0: String?, p1: BmobException?) {
                stopProgressDialog()
                if (p1 == null) {
                    listener.onSucceed(p0!!)
                } else {
                    toast(p1.message ?: "未知错误")
                    listener.onFailed(p1)
                }
            }
        })
    }

    fun BmobObject.updateData() {
        update(object: UpdateListener() {
            override fun done(p0: BmobException?) {
            }
        })
    }

    fun BmobObject.updateData(listener: UpdateDataListener) {
        startProgressDialog()
        update(object: UpdateListener() {
            override fun done(p0: BmobException?) {
                stopProgressDialog()
                if (p0 == null) {
                    listener.onSucceed()
                } else {
                    toast(p0.message ?: "未知错误")
                    listener.onFailed(p0)
                }
            }
        })
    }

    fun BmobObject.deleteData(objectId: String, listener: DeleteDataListener) {
        startProgressDialog()
        delete(objectId, object : UpdateListener() {
            override fun done(p0: BmobException?) {
                stopProgressDialog()
                if (p0 == null) {
                    listener.onSucceed()
                } else {
                    toast(p0.message ?: "")
                    listener.onFailed(p0)
                }
            }
        })
    }

    fun toast(msg: String)

    fun startProgressDialog()

    fun stopProgressDialog()
}