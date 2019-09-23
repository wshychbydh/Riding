package cool.eye.ridding.zone.contacts

import android.os.Bundle
import cool.eye.ridding.R
import cool.eye.ridding.broadcast.GuardService
import cool.eye.ridding.broadcast.ServiceUtil
import cool.eye.ridding.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_passenger_setting.*
import kotlinx.android.synthetic.main.common_title.*

/**
 * Created by cool on 17-3-1.
 */
class PassengerSettingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger_setting)
        iv_back.setOnClickListener { finish() }
        tv_title.text = getString(R.string.passenger_setting)
        btn_passenger_setting.setOnClickListener { saveSetting() }
        syncSetting()
    }

    fun syncSetting() {
        call_monitor.isChecked = PassengerUtils.isPassengerMonitor(this)
        black_list_call.isChecked = PassengerUtils.isBlackListCall(this)
        if (call_monitor.isChecked) {
            ServiceUtil.startService(this, GuardService::class.java)
        } else {
            ServiceUtil.stopService(this, GuardService::class.java)
        }
        finish()
    }

    fun saveSetting() {
        PassengerUtils.setPassengerMonitor(this, call_monitor.isChecked)
        PassengerUtils.setBlackListCall(this, black_list_call.isChecked)
    }
}