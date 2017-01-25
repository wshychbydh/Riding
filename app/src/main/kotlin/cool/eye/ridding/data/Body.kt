package cool.eye.ridding.data

internal enum class Body private constructor(val code: Int, val value: String) {
    UNFILLED(0, "未填"), FAT(1, "胖"), NORMAL(2, "正常"), THIN(3, "廋");


    companion object {

        fun valueOfCode(code: Int): Body {
            for (body in values()) {
                if (body.code == code) return body
            }
            return UNFILLED
        }
    }
}