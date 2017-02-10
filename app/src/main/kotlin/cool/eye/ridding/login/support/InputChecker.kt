package cn.sudiyi.app.client.account.support

import android.widget.EditText
import android.widget.Toast
import cool.eye.ridding.R

/**
 * Created by cool on 16-6-20.
 */
object InputChecker {

    fun checkUsername(usernameEt: EditText): Boolean {
        var username = usernameEt.text
        if (username.isNullOrEmpty()) {
            usernameEt.error = usernameEt.context.getString(R.string.account_username_hint)
            return false
        }
        return true
    }


    fun checkPassword(passwordEt: EditText): Boolean {
        var password = passwordEt.text
        if (password.isNullOrEmpty()) {
            passwordEt.error = passwordEt.context.getString(R.string.account_password_hint)
            return false
        }
        return true
    }

    fun checkPassword(passwordEt: EditText, passwordEt2: EditText): Boolean {
        var password = passwordEt.text.toString()
        var password2 = passwordEt2.text.toString()
        if (password2.isNullOrEmpty()) {
            passwordEt2.error = passwordEt.context.getString(R.string.account_password_hint2)
            return false
        } else if (!password2.equals(password)) {
            Toast.makeText(passwordEt.context, passwordEt.context.getString(R.string.account_password_different), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
