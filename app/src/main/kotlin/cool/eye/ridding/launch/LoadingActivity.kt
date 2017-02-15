package cool.eye.ridding.launch

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import cn.bmob.v3.BmobUser
import cn.bmob.v3.update.BmobUpdateAgent
import cool.eye.ridding.R
import cool.eye.ridding.login.ui.LoginActivity
import cool.eye.ridding.ui.HomeActivity

class LoadingActivity : AppCompatActivity() {

    var isJumped: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        BmobUpdateAgent.setUpdateListener { updateStatus, updateInfo ->
            handler.removeMessages(0)
            //根据updateStatus来判断更新是否成功
            if (!(updateInfo?.isforce ?: false) && !isJumped) {
                doNext()
            }
        }
        BmobUpdateAgent.update(this)
        handler.sendEmptyMessageDelayed(0, 5000)
    }

    fun doNext() {
        isJumped = true
        startActivity(Intent(this@LoadingActivity,
                if (BmobUser.getCurrentUser() == null)
                    LoginActivity::class.java
                else
                    HomeActivity::class.java))
        finish()
    }

    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            doNext()
        }
    }
}
