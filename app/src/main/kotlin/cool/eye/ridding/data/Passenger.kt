package cool.eye.ridding.data

import com.alibaba.fastjson.JSON

/**
 * Created by cool on 17-1-16.
 */
class Passenger : BaseBmobObject() {
    var name = NAME_DEFAULT
    var phone: String = ""
    var sex = SEX.MAN.code//备用字段
    var age = "" //未填
    var promise_not: Int = 0//不守信次数
    var by_count = 0 //乘坐次数
    var job: String = ""
    var remark = ""

    companion object {
        const val NAME_DEFAULT = "乘客"

        fun toJson(passenger: Passenger): String {
            return JSON.toJSONString(passenger)
        }

        fun parseJson(json: String): Passenger {
            return JSON.parseObject(json, Passenger::class.java)
        }
    }

    override fun toString(): String {
        return "Passenger(age='$age', name='$name', phone='$phone', sex=$sex, promise_not=$promise_not, by_count=$by_count, job='$job', remark='$remark')"
    }

    fun blackList(): String {
        return "黑名单=>$name \n备注：$remark"
    }

    fun call(): String {
        if (remark.isNullOrEmpty()){
            return "乘客去电=>$name"
        } else {
            return "乘客去电=>$name\n备注：$remark"
        }
    }

    fun receive(): String {
        if (remark.isNullOrEmpty()){
            return "乘客来电=>$name"
        } else {
            return "乘客来电=>$name\n备注：$remark"
        }
    }
}
