package cooleye.eot.kotlin.data

import cn.bmob.v3.BmobObject

/**
 * Created by cool on 16-11-29.
 */
open class Test: BmobObject() {
    lateinit var test: String

    fun setValue(value: String) {
        test = value
    }

    fun getValue(): String {
        return test
    }

}