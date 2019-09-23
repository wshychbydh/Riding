package cool.eye.ridding.data

internal enum class AGE private constructor(val code: Int, val value: String) {
    UNFILLED(0, "未填"), /*0~10*/ SMALL(1, "0~6"), /*11~55*/ YOUNG(2, "6~55"), /*55~*/ OLD(3, "55以上");


    companion object {

        fun valueOfCode(code: Int): AGE {
            for (age in values()) {
                if (age.code == code) return age
            }
            return UNFILLED
        }
    }
}

