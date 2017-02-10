package cool.eye.ridding.data

internal enum class SEX private constructor(val code: Int, val value: String) {
    MAN(0, "男"), WOMAN(1, "女");


    companion object {

        fun valueOfCode(code: Int): SEX {
            return values().firstOrNull { it.code == code } ?: MAN
        }
    }
}