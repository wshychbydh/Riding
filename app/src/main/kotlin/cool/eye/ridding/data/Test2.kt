package cooleye.eot.kotlin.data

internal object Test2 {

    private lateinit var test: String

    fun setValue(value: String) {
        test = value
    }

    fun getValue(): String {
        return test
    }

    @JvmStatic fun main(args: Array<String>) {
        val test = Test2
        setValue("fdsfdsfd")
        println(getValue())

    }
}