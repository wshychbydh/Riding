package cool.eye.ridding.zone.contacts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import cool.eye.ridding.R
import cool.eye.ridding.data.Passenger
import cool.eye.ridding.db.DBHelper
import cool.eye.ridding.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_add_passenger.*
import kotlinx.android.synthetic.main.common_title.*


class PassengerAddActivity : BaseActivity() {

    var passenger: Passenger? = null

    companion object {

        const val PASSENGER = "passenger"

        fun launch(activity: Activity, passenger: Passenger?) {
            var intent = Intent(activity, PassengerAddActivity::class.java)
            if (passenger != null) {
                intent.putExtra(PASSENGER, passenger)
            }
            activity.startActivityForResult(intent, 1001)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_passenger)
        tv_title.text = getString(R.string.passenger_add)
        iv_back.setOnClickListener { finish() }
        submit.setOnClickListener { submit() }
        (radiogroup_blacklist.getChildAt(0) as RadioButton).isChecked = true
        passenger = intent.getSerializableExtra(PASSENGER) as? Passenger
        if (passenger != null) fillPassenger()

        passenger_check.setOnClickListener {
            var phone = passenger_phone.text.toString()
            if (phone.isNullOrEmpty()) return@setOnClickListener
            startProgressDialog()
            var query = BmobQuery<Passenger>()
            query.addWhereEqualTo("phone", phone)
            query.addWhereEqualTo("userId", BmobUser.getCurrentUser().objectId)
            query.findObjects(object : FindListener<Passenger>() {
                override fun done(p0: MutableList<Passenger>?, p1: BmobException?) {
                    stopProgressDialog()
                    if (p0?.isNotEmpty() ?: false) {
                        passenger = p0!![0]
                        fillPassenger()
                    }
                }
            })
        }

        radiogroup_blacklist.setOnCheckedChangeListener { group, checkedId ->
            black_list_share.visibility = if (group.indexOfChild(group.findViewById(checkedId)) == 1)
                View.VISIBLE else View.INVISIBLE
        }
    }

    fun fillPassenger() {
        passenger_by_count.setText(passenger!!.by_count.toString())
        passenger_name.setText(passenger!!.name)
        (radiogroup_sex.getChildAt(passenger!!.sex) as RadioButton).isChecked = true
        passenger_age.setText(passenger!!.age)
        passenger_phone.setText(passenger!!.phone)
        var blackList = passenger!!.promise_not > 0
        (radiogroup_blacklist.getChildAt(if (blackList) 1 else 0) as RadioButton).isChecked = true
        black_list_share.visibility = if (blackList) View.VISIBLE else View.INVISIBLE
        passenger_remark.setText(passenger!!.remark)
    }

    fun submit() {
        val name = passenger_name.text.toString().trim()
        if (name.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.name_empty), Toast.LENGTH_SHORT).show()
            return
        }
        val sex = radiogroup_sex.indexOfChild(radiogroup_sex.findViewById(radiogroup_sex.checkedRadioButtonId))
        if (sex == -1) {
            Toast.makeText(this, getString(R.string.sex_empty), Toast.LENGTH_SHORT).show()
            return
        }
        val phone = passenger_phone.text.toString().trim()
        if (phone.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.phone_empty), Toast.LENGTH_SHORT).show()
            return
        }

        var blackList = radiogroup_blacklist.indexOfChild(radiogroup_blacklist.findViewById(radiogroup_blacklist.checkedRadioButtonId)) == 1

        if (passenger == null) passenger = Passenger()
        passenger!!.name = name
        passenger!!.sex = sex
        passenger!!.age = passenger_age.text.toString().trim()
        passenger!!.job = passenger_job.text.toString().trim()
        passenger!!.phone = phone
        var byCount = passenger_by_count.text.toString().trim()
        passenger!!.by_count = if (byCount.isNullOrEmpty()) 0 else byCount.toInt()
        passenger!!.promise_not = if (blackList) 1 else 0
        passenger!!.remark = passenger_remark.text.toString().trim()
        savePassenger()
        saveBlackList()
    }

    private fun saveBlackList() {
        if (black_list_share.isChecked) {
            PassengerUtils.saveBlackList(passenger!!)
        }
    }

    private fun savePassenger() {
        startProgressDialog()
        DBHelper.get(this).savePassenger(passenger!!)
        if (passenger!!.objectId == null) {
            var bmobQuery = BmobQuery<Passenger>()
            bmobQuery.addWhereEqualTo("userId", BmobUser.getCurrentUser().objectId)
            bmobQuery.addWhereEqualTo("phone", passenger!!.phone)
            bmobQuery.findObjects(object : FindListener<Passenger>() {
                override fun done(p0: MutableList<Passenger>?, p1: BmobException?) {
                    if (p1 == null) {
                        passenger!!.objectId = p0!![0].objectId
                        passenger!!.promise_not += p0[0].promise_not
                        passenger!!.by_count += p0[0].by_count
                        updatePassenger()
                    } else {
                        passenger!!.save(object : SaveListener<String?>() {
                            override fun done(p0: String?, p1: BmobException?) {
                                if (p1 == null) {
                                    stopProgressDialog()
                                    setResult(1001)
                                    finish()
                                } else {
                                    toast(p1.message ?: "")
                                }
                            }
                        })
                    }
                }
            })
        } else {
            updatePassenger()
        }
    }

    fun updatePassenger() {
        passenger!!.update(passenger!!.objectId, object : UpdateListener() {
            override fun done(p0: BmobException?) {
                stopProgressDialog()
                setResult(1001)
                finish()
            }
        })
    }
}
