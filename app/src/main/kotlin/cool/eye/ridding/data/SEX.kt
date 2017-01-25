package cool.eye.ridding.data

internal enum class SEX private constructor(val code: Int, val value: String) {
    UNFILLED(0, "未填"), MAN(1, "男"), WOMAN(2, "女");


    companion object {

        fun valueOfCode(code: Int): SEX {
            for (sex in values()) {
                if (sex.code == code) return sex
            }
            return UNFILLED
        }
    }
}