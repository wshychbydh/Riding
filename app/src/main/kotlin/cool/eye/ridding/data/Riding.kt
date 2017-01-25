package cool.eye.ridding.data

/**
 * Created by cool on 17-1-16.
 */

class Riding : BaseBmobObject() {
    var carryId: String = ""
    var passenger: Passenger? = null
    var peopleCount: Int = 1
    var goOffTime: String = ""//车主的出发时间，作为查询关键字
    var ridingTime: String = ""
    var startAddress: String? = null
    var endAddress: String? = null
    var mark: String? = null

    fun peopleCount(): String {
        return "$peopleCount 人"
    }

    fun composeAddress(): String {
        if (startAddress == null || endAddress == null) {
            return ""
        }
        return "$startAddress － $endAddress"
    }

    override fun toString(): String {
        return "Riding(carryId='$carryId', passenger=$passenger, peopleCount=$peopleCount, goOffTime='$goOffTime', ridingTime='$ridingTime', startAddress=$startAddress, endAddress=$endAddress, mark=$mark)"
    }

}
