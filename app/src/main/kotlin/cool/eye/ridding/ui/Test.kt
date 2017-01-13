package cool.eye.ridding.ui

import android.view.View

/**
 * Created by cool on 17-1-13.
 */

class Test {
    private fun test() {
        val l = arrayOf(111L, 111L)
        val vIew: View? = null
        vIew!!.setOnLongClickListener {
            println()
            false
        }
    }
}
