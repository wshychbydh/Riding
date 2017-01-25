package cool.eye.ridding.data

/**
 * Created by cool on 17-1-16.
 */
class Address : BaseBmobObject() {
    var name: String? = null
    var city: String? = null //备用字段
    var longitude: Double? = null//备用字段
    var latitude: Double? = null//备用字段
    var ridingCount: Int = 0 //乘客使用次数
    var carryCount: Int = 0 //司机使用次数
    override fun toString(): String {
        return "Address(name=$name, city=$city, longitude=$longitude, latitude=$latitude)"
    }
}