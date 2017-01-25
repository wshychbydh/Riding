package cool.eye.ridding.util

import cn.bmob.v3.exception.BmobException

/**
 * Created by cool on 17-1-18.
 */
interface SaveDataListener {
    fun onSucceed(objectId:String)
    fun onFailed(error: BmobException){}
}