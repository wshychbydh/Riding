package cool.eye.ridding.login.ui

import android.os.Bundle
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.sudiyi.app.client.account.support.InputChecker
import cool.eye.ridding.R
import cool.eye.ridding.login.model.UserModel
import cool.eye.ridding.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.common_title.*

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        iv_back.setOnClickListener { finish() }
        tv_title.text = getString(R.string.account_register)
        btn_register.setOnClickListener { register() }
    }

    fun register() {
        if (inputCheck()) {
            startProgressDialog()
            var user = UserModel()
            user.username = et_username.text.toString()
            user.setPassword(et_password.text.toString())
            user.signUp(object : SaveListener<UserModel?>() {
                override fun done(user: UserModel?, exception: BmobException?) {
                    stopProgressDialog()
                    if (exception == null) {
                        toast(getString(R.string.account_register_success))
                        LoginActivity.launch(this@RegisterActivity, et_username.text.toString(), et_password.text.toString())
                        finish()
                    } else {
                        toast(exception.message ?: "")
                    }
                }
            })
        }
    }

    private fun inputCheck(): Boolean {
        return InputChecker.checkUsername(et_username)
                && InputChecker.checkPassword(et_password)
                && InputChecker.checkPassword(et_password, et_password2)
    }
}
