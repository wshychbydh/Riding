package cool.eye.ridding.zone.contacts

import android.content.Intent
import android.os.Bundle
import cool.eye.ridding.R
import cool.eye.ridding.adapter.FragmentTablePagerAdapter
import cool.eye.ridding.db.DBHelper
import cool.eye.ridding.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.common_title.*

class ContactsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //初始化数据库
        DBHelper.initSQLite(this)
        setContentView(R.layout.activity_contacts)
        iv_back.setOnClickListener { finish() }
        iv_submit.setImageResource(R.drawable.add)
        iv_submit.setOnClickListener {
            PassengerAddActivity.launch(this, null)
        }
        tv_title.text = getString(R.string.contacts)
        loadContacts()
    }

    fun loadContacts() {
        var adapter = FragmentTablePagerAdapter(this)
        var bundle = Bundle()
        bundle.putInt(PassengerFragment.PASSENGER_TYPE, PassengerFragment.PASSENGER)
        adapter.addTab(PassengerFragment::class.java, bundle, getString(R.string.passenger))
        bundle = Bundle()
        bundle.putInt(PassengerFragment.PASSENGER_TYPE, PassengerFragment.BLACK_LIST)
        adapter.addTab(PassengerFragment::class.java, bundle, getString(R.string.black_list))
        passenger_viewpager.adapter = adapter
        contacts_pager_tabstrip.tabIndicatorColor = resources.getColor(R.color.orange)
        passenger_viewpager.currentItem = intent.getIntExtra(PassengerFragment.PASSENGER_TYPE, PassengerFragment.PASSENGER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadContacts()
    }
}
