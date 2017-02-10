package cool.eye.ridding.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bmob.v3.BmobUser
import cool.eye.ridding.R
import cool.eye.ridding.login.model.UserModel
import cool.eye.ridding.login.ui.LoginActivity
import cool.eye.ridding.zone.card.ui.CaptureActivity
import cool.eye.ridding.zone.card.ui.CardActivity
import cool.eye.ridding.zone.contacts.ContactsActivity
import cool.eye.ridding.zone.contacts.PassengerFragment
import kotlinx.android.synthetic.main.fragment_zone.*

/**
 * Created by cool on 17-1-12.
 */
class UserZoneFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val View = inflater?.inflate(R.layout.fragment_zone, container, false)
        return View
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        draweeview.setImageURI(BmobUser.getCurrentUser(UserModel::class.java).head)
        card.setOnClickListener { startActivity(Intent(activity, CardActivity::class.java)) }
        scan.setOnClickListener { startActivity(Intent(activity, CaptureActivity::class.java)) }
        book.setOnClickListener { toPassengerActivity(PassengerFragment.PASSENGER) }
        black_list.setOnClickListener { toPassengerActivity(PassengerFragment.BLACK_LIST) }
        logout.setOnClickListener {
            BmobUser.logOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity.finish()
        }
    }

    fun toPassengerActivity(type: Int) {
        var intent = Intent(activity, ContactsActivity::class.java)
        intent.putExtra(PassengerFragment.PASSENGER_TYPE, type)
        startActivity(intent)
    }
}