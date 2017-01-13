package cooleye.eot.kotlin.data

import java.io.Serializable

/**
 * Created by cool on 16-11-25.
 */
data class Passenger(
        var name: String = "乘客",
        var sex: SEX = Passenger.SEX.UNFILLED,
        var body: Body = Passenger.Body.UNFILLED,
        var age: AGE = Passenger.AGE.UNFILLED,
        var phone: String) : Serializable {

    enum class SEX(var value: String) {
        UNFILLED("未填"), MAN("男"), WOMAN("女")
    }

    enum class Body(var value: String) {
        UNFILLED("未填"), FAT("胖"), NORMAL("正常"), THIN("廋")
    }

    enum class AGE(var value: String) {
        UNFILLED("未填"), /*0~10*/ SMALL("0~6"), /*11~55*/ YOUNG("6~55"), /*55~*/ OLD("55以上")
    }
}