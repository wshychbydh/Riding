package cool.eye.ridding.data

import cn.bmob.v3.BmobObject
import cn.bmob.v3.BmobUser
import com.google.gson.Gson

/**
 * Created by cool on 17-1-16.
 */
class BlackList : BmobObject() {

    var name = "黑名单"
    var phone: String = ""
    var sex = SEX.MAN.code//备用字段
    var age = "" //未填
    var job: String = ""
    var signCount: Int = 0 //被标记的次数
    var signUser: MutableList<String>? = null //标记的人的id
    var remark: MutableList<String>? = null //被标记的内容

    companion object {

        fun toJson(blackList: BlackList): String {
            return Gson().toJson(blackList)
        }

        fun parseJson(json: String): BlackList {
            return Gson().fromJson(json, BlackList::class.java)
        }
    }

    fun getRemark(): String {
        var index = signUser?.indexOf(BmobUser.getCurrentUser().objectId) ?: -1
        return remark?.get(if (index > -1)index else 0) ?: ""
    }

    fun blackList(): String {
        var remark = getRemark()
        if (remark.isNullOrEmpty()) {
            return "黑名单=>$name"
        } else {
            return "黑名单=>$name\n备注：$remark"
        }
    }

    fun passenger(): String {
        var remark = getRemark()
        if (remark.isNullOrEmpty()) {
            return "乘客=>$name"
        } else {
            return "乘客=>$name\n备注：$remark"
        }
    }

    override fun toString(): String {
        return "BlackList(age='$age', name='$name', phone='$phone', sex=$sex, job='$job', signCount=$signCount, signUser=$signUser, remark=$remark)"
    }

}
