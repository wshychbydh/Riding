package cool.eye.ridding.data

/**
 * Created by cool on 17-1-20.
 */
enum class CarryStatus {
    UNDERWAY(0, "待完成"),FINISHED(1, "已完成");

    var code: Int
    var value: String

    constructor(code: Int, value: String) {
        this.code = code
        this.value = value
    }
}