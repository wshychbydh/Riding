package cool.eye.ridding.util

import cn.bmob.v3.exception.BmobException

/**
 * Created by cool on 17-1-18.
 */
interface UpdateDataListener {
    fun onSucceed()
    fun onFailed(error: BmobException){}
}