package cool.eye.ridding.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bmob.v3.BmobUser
import cn.bmob.v3.update.BmobUpdateAgent
import cn.sudiyi.app.client.account.page.FeedbackActivity
import cool.eye.ridding.BuildConfig
import cool.eye.ridding.R
import cool.eye.ridding.login.model.UserModel
import cool.eye.ridding.login.ui.LoginActivity
import cool.eye.ridding.login.ui.SetPasswordActivity
import cool.eye.ridding.zone.about.AboutActivity
import cool.eye.ridding.zone.card.ui.CaptureActivity
import cool.eye.ridding.zone.card.ui.CardActivity
import cool.eye.ridding.zone.contacts.ContactsActivity
import cool.eye.ridding.zone.contacts.PassengerFragment
import cool.eye.ridding.zone.userinfo.UserInfoActivity
import kotlinx.android.synthetic.main.fragment_zone.*

/**
 * Created by cool on 17-1-12.
 */
class UserZoneFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_zone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app_version.text = "当前版本: v${BuildConfig.VERSION_NAME}"
        fillUserInfo()
        setting.setOnClickListener { startActivityForResult(Intent(activity, UserInfoActivity::class.java), 1001) }
        card.setOnClickListener { startActivity(Intent(activity, CardActivity::class.java)) }
        scan.setOnClickListener { startActivity(Intent(activity, CaptureActivity::class.java)) }
        book.setOnClickListener { toPassengerActivity(PassengerFragment.PASSENGER) }
        black_list.setOnClickListener { toPassengerActivity(PassengerFragment.BLACK_LIST) }
        password_modify.setOnClickListener { startActivity(Intent(activity, SetPasswordActivity::class.java)) }
        feedback.setOnClickListener { startActivity(Intent(activity, FeedbackActivity::class.java)) }
        about.setOnClickListener { startActivity(Intent(activity, AboutActivity::class.java)) }
        check_update.setOnClickListener {  BmobUpdateAgent.update(context) }
        logout.setOnClickListener {
            BmobUser.logOut()
          //  ServiceUtil.stopService(context,GuardService::class.java)
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }
    }

    private fun fillUserInfo() {
        var user = BmobUser.getCurrentUser(UserModel::class.java)
        if (user.nickname.isNullOrEmpty()) {
            user_name.visibility = View.GONE
        } else {
            user_name.text = user.nickname
        }
    }

    private fun toPassengerActivity(type: Int) {
        var intent = Intent(activity, ContactsActivity::class.java)
        intent.putExtra(PassengerFragment.PASSENGER_TYPE, type)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == 1001) {
            fillUserInfo()
        }
    }
}