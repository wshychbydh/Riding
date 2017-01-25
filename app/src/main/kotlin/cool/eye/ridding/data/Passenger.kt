package cool.eye.ridding.data

/**
 * Created by cool on 17-1-16.
 */
class Passenger : BaseBmobObject() {
    var name = NAME_DEFAULT
    var phone: String = ""
    var sex = SEX.UNFILLED.code //备用字段
    var body = Body.UNFILLED.code
    var age = AGE.UNFILLED.code
    var by_count = 0 //乘坐次数

    companion object {
        const val NAME_DEFAULT = "乘客"
    }

    override fun toString(): String {
        return "Passenger(name='$name', phone='$phone', sex=$sex, body=$body, age=$age, by_count=$by_count)"
    }

}
