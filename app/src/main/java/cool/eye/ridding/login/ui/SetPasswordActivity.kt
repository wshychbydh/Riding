package cool.eye.ridding.login.ui

import android.os.Bundle
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import cool.eye.ridding.login.support.InputChecker
import cool.eye.ridding.R
import cool.eye.ridding.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_set_password.*
import kotlinx.android.synthetic.main.common_title.*


/**
 * Created by cool on 16-6-15.
 */
class SetPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_password)
        iv_back.setOnClickListener { finish() }
        tv_title.text = getString(R.string.account_password_modify)
        btn_reset_password.setOnClickListener { setPassword() }
    }

    private fun setPassword() {
        if (checkInput()) {
            BmobUser.updateCurrentUserPassword(reset_password_old.text.trim().toString(), reset_password_new.text.trim().toString(), object : UpdateListener() {

                override fun done(e: BmobException?) {
                    if (e == null) {
                        toast("密码修改成功")
                        finish()
                    } else {
                        toast(e.message ?: "")
                    }
                }

            })
        }
    }

    fun checkInput(): Boolean {
        return InputChecker.checkPassword(reset_password_old)
                && InputChecker.checkPassword(reset_password_new)
                && InputChecker.checkPassword(reset_password_new, reset_password_new2)
    }
}
