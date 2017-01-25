package cool.eye.ridding.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.bmob.v3.BmobUser
import cool.eye.ridding.login.ui.LoginActivity

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BmobUser.getCurrentUser() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            HomeActivity.launch(this, 0)
        }
    }
}
