package cool.eye.ridding.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatEditText
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import cool.eye.ridding.R
import cool.eye.ridding.data.CarryInfo
import cool.eye.ridding.data.Passenger
import cool.eye.ridding.data.Riding
import cool.eye.ridding.db.BmobHelper
import cool.eye.ridding.ui.dialog.DatePickerDialog
import cool.eye.ridding.util.SaveDataListener
import cool.eye.ridding.util.UpdateDataListener
import cool.eye.ridding.util.Utils
import kotlinx.android.synthetic.main.activity_add_riding.*
import kotlinx.android.synthetic.main.common_title.*


class RidingAddActivity : BaseActivity() {

    lateinit var carryInfo: CarryInfo
    lateinit var addressEt: AppCompatEditText
    var riding: Riding? = null

    companion object {

        const val REMAIN_COUNT = "remain_count"
        const val CARRY_INFO = "carry_info"
        const val RIDING_INFO = "riding_info"

        fun launch(fragment: RidingFragment, remainCount: Int, carryInfo: CarryInfo, riding: Riding?) {
            var intent = Intent(fragment.context, RidingAddActivity::class.java)
            intent.putExtra(REMAIN_COUNT, remainCount)
            intent.putExtra(CARRY_INFO, carryInfo)
            if (riding != null) {
                intent.putExtra(RIDING_INFO, riding)
            }
            fragment.startActivityForResult(intent, 1001)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_riding)
        tv_title.text = getString(R.string.riding_add)
        iv_back.setOnClickListener { finish() }
        submit.setOnClickListener { submit() }
        ridding_time.setOnClickListener {
            var dialog = DatePickerDialog()
            dialog.onDateSelectedListener = {
                time ->
                ridding_time.text = time
            }
            dialog.currentTime = ridding_time.text.toString()
            dialog.show(supportFragmentManager, null)
        }
        start_address_common.setOnClickListener {
            setAddress(start_address)
        }
        end_address_common.setOnClickListener {
            setAddress(end_address)
        }

        passenger_check.setOnClickListener {
            var phone = passenger_phone.text.toString()
            if (phone.isNullOrEmpty()) return@setOnClickListener
            startProgressDialog()
            passenger_notice.visibility = View.GONE
            var query = BmobQuery<Passenger>()
            query.addWhereEqualTo("phone", phone)
            query.addWhereEqualTo("userId", BmobUser.getCurrentUser().objectId)
            query.findObjects(object : FindListener<Passenger>() {
                override fun done(p0: MutableList<Passenger>?, p1: BmobException?) {
                    stopProgressDialog()
                    if (p0?.isNotEmpty() ?: false) {
                        fillPassenger(p0!![0])
                    }
                }
            })
        }

        var peopleCount = Array(intent.getIntExtra(REMAIN_COUNT, 4), { i -> "${i + 1} 人" })
        people_count.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, peopleCount)

        carryInfo = intent.getSerializableExtra(CARRY_INFO) as CarryInfo
        riding = intent.getSerializableExtra(RIDING_INFO) as? Riding

        if (riding == null) {
            start_address.setTextAndSelectEnd(carryInfo.startAddress!!)
            end_address.setText(carryInfo.endAddress!!)
            ridding_time.text = carryInfo.goOffTime
        } else {
            fillPassenger(riding!!.passenger!!)
            start_address.setTextAndSelectEnd(riding!!.startAddress!!)
            end_address.setText(riding!!.endAddress!!)
            ridding_time.text = riding!!.ridingTime
            people_count.setSelection(riding!!.peopleCount - 1)
            riding_mark.setText(riding!!.mark)
        }

        price.setText("${carryInfo.price}")
        passenger_name.setSelection(passenger_name.text.length)
    }

    fun fillPassenger(passenger: Passenger) {
        if (passenger.remark.isNotEmpty()) {
            passenger_notice.visibility = View.VISIBLE
            passenger_notice.text = passenger.remark
        } else if (passenger.by_count > 0) {
            passenger_notice.visibility = View.VISIBLE
            passenger_notice.text = getString(R.string.by_count, passenger.by_count)
        }
        passenger_name.setText(passenger.name)
        (radiogroup_sex.getChildAt(passenger.sex) as RadioButton).isChecked = true
        passenger_age.setText(passenger.age)
        passenger_job.setText(passenger.job)
        passenger_phone.setText(passenger.phone)
    }

    fun setAddress(requestEt: AppCompatEditText) {
        addressEt = requestEt
        var intent = Intent(this, AddressActivity::class.java)
        startActivityForResult(intent, AddressActivity.REQUEST_CODE)
    }


    fun AppCompatEditText.setTextAndSelectEnd(text: String) {
        setText(text)
        setSelection(text.length)
    }

    fun submit() {
        val name = passenger_name.text.trim().toString()
        if (name.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.name_empty), Toast.LENGTH_SHORT).show()
            return
        }
        val sex = radiogroup_sex.indexOfChild(radiogroup_sex.findViewById(radiogroup_sex.checkedRadioButtonId))
        if (sex == -1) {
            Toast.makeText(this, getString(R.string.sex_empty), Toast.LENGTH_SHORT).show()
            return
        }

        val startAds = start_address.text.trim().toString()
        if (startAds.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.start_address_empty), Toast.LENGTH_SHORT).show()
            return
        }
        val endAds = end_address.text.trim().toString()
        if (endAds.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.end_address_empty), Toast.LENGTH_SHORT).show()
            return
        }
        val phone = passenger_phone.text.trim().toString()
        if (phone.isNullOrEmpty() || !Utils.isPhoneNumberValid(phone)) {
            Toast.makeText(this, getString(R.string.phone_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (riding == null) {
            riding = Riding()
            riding!!.passenger = Passenger()
            riding!!.passenger!!.by_count = 1
            riding!!.carryId = carryInfo.objectId
        }

        riding!!.passenger!!.name = name
        riding!!.passenger!!.sex = sex
        riding!!.passenger!!.age = passenger_age.text.toString()
        riding!!.passenger!!.job = passenger_job.text.toString()
        riding!!.passenger!!.phone = phone

        riding!!.goOffTime = carryInfo.goOffTime!!
        riding!!.ridingTime = ridding_time.text.toString()
        riding!!.peopleCount = people_count.selectedItemPosition + 1
        riding!!.startAddress = startAds
        riding!!.endAddress = endAds
        riding!!.mark = riding_mark.text.toString()
        saveAddress(startAds, endAds)
        saveData(riding!!)
    }

    fun saveAddress(startAddress: String, endAddress: String) {
        var bmobHelper = BmobHelper(this)
        bmobHelper.saveAddress(startAddress, 0)
        bmobHelper.saveAddress(endAddress, 0)
    }

    fun saveData(riding: Riding) {
        if (riding.passenger!!.objectId == null) {
            startProgressDialog()
            var bmobQuery = BmobQuery<Passenger>()
            bmobQuery.addWhereEqualTo("phone", riding.passenger!!.phone)
            bmobQuery.addWhereEqualTo("userId", BmobUser.getCurrentUser().objectId)
            bmobQuery.findObjects(object : FindListener<Passenger>() {
                override fun done(p0: MutableList<Passenger>?, p1: BmobException?) {
                    if (p1 == null) {
                        riding.passenger!!.objectId = p0!![0].objectId
                        riding.passenger!!.promise_not = p0[0].promise_not
                        riding.passenger!!.by_count = p0[0].by_count + 1
                        updatePassenger(riding.passenger!!)
                        saveRiding(riding)
                    } else {
                        riding.passenger!!.save(object : SaveListener<String?>() {
                            override fun done(p0: String?, p1: BmobException?) {
                                if (p1 == null) {
                                    saveRiding(riding)
                                } else {
                                    stopProgressDialog()
                                    toast(p1.message ?: "")
                                }
                            }
                        })
                    }
                }
            })
        } else {
            updatePassenger(riding.passenger!!)
            saveRiding(riding)
        }
    }

    fun updatePassenger(passenger: Passenger) {
        passenger.update(object : UpdateListener() {
            override fun done(p0: BmobException?) {
            }
        })
    }

    fun saveRiding(riding: Riding) {
        if (riding.objectId == null) {
            riding.saveData(object : SaveDataListener {
                override fun onSucceed(objectId: String) {
                    toast(getString(R.string.riding_add_succeed))
                    setResult(1001)
                    finish()
                }
            })
        } else {
            riding.updateData(object : UpdateDataListener {
                override fun onSucceed() {
                    toast(getString(R.string.riding_update_succeed))
                    setResult(1001)
                    finish()
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == AddressActivity.REQUEST_CODE && resultCode == AddressActivity.REQUEST_CODE) {
            addressEt.setText(data.getStringExtra(AddressActivity.ADDRESS))
        }
    }
}
