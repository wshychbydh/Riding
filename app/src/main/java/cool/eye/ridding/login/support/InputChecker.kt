package cool.eye.ridding.login.support

import android.widget.EditText
import android.widget.Toast
import cool.eye.ridding.R
import cool.eye.ridding.util.Utils

/**
 * Created by cool on 16-6-20.
 */
object InputChecker {

    fun checkPhone(phoneEt: EditText): Boolean {
        var phone = phoneEt.text.toString()
        if (!Utils.isPhoneNumber(phone)) {
            phoneEt.error = phoneEt.context.getString(R.string.account_phone_error)
            return false
        }
        return true
    }

    fun checkEmail(emailEt: EditText): Boolean {
        var email = emailEt.text.toString()
        if (!Utils.isEmail(email)) {
            emailEt.error = emailEt.context.getString(R.string.account_email_error)
            return false
        }
        return true
    }

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
