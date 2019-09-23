package cool.eye.ridding.data

/**
 * Created by cool on 17-1-16.
 */

/**
 * 车主发布的载客信息
 */
class CarryInfo : BaseBmobObject() {
    var startAddress: String? = null
    var endAddress: String? = null
    var goOffTime: String? = null
    var peopleCount: Int = 4
    var price: Int = 60
    var phone: String? = null
    var mark: String? = null
    var status: Int = 0  //0进行中，１已完成载客

    fun composeAddress(): String {
        if (startAddress == null || endAddress == null) {
            return ""
        }
        return "$startAddress － $endAddress"
    }

    fun composePrice():String{
        return "$price 元/人"
    }

    fun composeCount():String{
        return "预载${peopleCount}人"
    }

    override fun toString(): String {
        return "CarryInfo(startAddress=$startAddress, endAddress=$endAddress, goOffTime='$goOffTime', peopleCount=$peopleCount, price=$price, phone='$phone', mark=$mark, status=$status)"
    }

}
