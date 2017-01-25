package cool.eye.ridding.login.ui

import android.os.Bundle
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.sudiyi.app.client.account.support.InputChecker
import cool.eye.ridding.R
import cool.eye.ridding.login.model.UserModel
import cool.eye.ridding.ui.BaseActivity
import cool.eye.ridding.ui.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.common_title.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tv_title.text = "登录"
        btn_login.setOnClickListener { login() }
    }

    fun login() {
        if (inputCheck()) {
            startProgressDialog()
            var user = UserModel()
            user.username = et_username.text.toString()
            user.setPassword(et_password.text.toString())
            user.login(object : SaveListener<BmobUser>() {
                override fun done(p0: BmobUser?, p1: BmobException?) {
                    stopProgressDialog()
                    if (p1 == null) {
                        //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                        //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                        toast(getString(R.string.login_success))
                        HomeActivity.launch(this@LoginActivity, 0)
                    } else {
                        toast(p1?.message ?: "")
                    }
                }
            })
        }
    }

    private fun inputCheck(): Boolean {
        if (InputChecker.checkUsername(et_username)
                && InputChecker.checkPassword(et_password)) {
            return true
        }
        return false
    }
}
