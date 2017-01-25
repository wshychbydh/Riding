package cn.sudiyi.app.client.account.support

import android.widget.EditText

/**
 * Created by cool on 16-6-20.
 */
object InputChecker {

    fun checkUsername(usernameEt: EditText): Boolean {
        return !usernameEt.text.isNullOrEmpty()
    }


    fun checkPassword(passwordEt: EditText): Boolean {
        return !passwordEt.text.isNullOrEmpty()
    }
}
