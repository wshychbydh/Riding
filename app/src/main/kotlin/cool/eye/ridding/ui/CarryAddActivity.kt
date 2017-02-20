package cool.eye.ridding.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatEditText
import android.view.View
import cn.bmob.v3.BmobUser
import cool.eye.ridding.R
import cool.eye.ridding.data.CarryInfo
import cool.eye.ridding.db.BmobHelper
import cool.eye.ridding.ui.dialog.DatePickerDialog
import cool.eye.ridding.util.SaveDataListener
import cool.eye.ridding.util.UpdateDataListener
import kotlinx.android.synthetic.main.activity_carry.*
import kotlinx.android.synthetic.main.common_title.*

/**
 * Created by cool on 17-1-17.
 */
class CarryAddActivity : BaseActivity() {

    lateinit var addressEt: AppCompatEditText
    var objectId: String? = null

    companion object {
        const val CARRY_INFO = "carry_info"
        fun launch(fragment: RidingFragment, carryInfo: CarryInfo?) {
            var intent = Intent(fragment.context, CarryAddActivity::class.java)
            if (carryInfo != null)
                intent.putExtra(CARRY_INFO, carryInfo)
            fragment.startActivityForResult(intent, 1001)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carry)
        iv_back.setOnClickListener { finish() }
        tv_title.text = getString(R.string.car_submit)
        var carryInfo = intent.getSerializableExtra(CARRY_INFO) as CarryInfo?

        if (carryInfo != null) {
            objectId = carryInfo.objectId
            carry_start_address.setText(carryInfo.startAddress)
            carry_end_address.setText(carryInfo.endAddress)
            carry_go_off.text = carryInfo.goOffTime
            carry_people_count.setText(carryInfo.peopleCount.toString())
            carry_price.setText(carryInfo.price.toString())
            carry_phone.setText(carryInfo.phone)
            carry_mark.setText(carryInfo.mark)
        }

        carry_go_off.setOnClickListener {
            var dialog = DatePickerDialog()
            dialog.onDateSelectedListener = {
                time ->
                carry_go_off.text = time
            }
            dialog.currentTime = carry_go_off.text.toString()
            dialog.show(supportFragmentManager,null)
        }
        carry_save.setOnClickListener { save() }

        carry_start_address_common.setOnClickListener { v ->
            setAddress(carry_start_address)
        }
        carry_end_address_common.setOnClickListener { v ->
            setAddress(carry_end_address)
        }
        carry_phone.setText(BmobUser.getCurrentUser().mobilePhoneNumber)
    }

    fun setAddress(requestEt: View) {
        addressEt = requestEt as AppCompatEditText
        var intent = Intent(this, AddressActivity::class.java)
        startActivityForResult(intent, AddressActivity.REQUEST_CODE)
    }

    fun save() {
        val startAds = carry_start_address.text.trim().toString()
        if (startAds.isNullOrEmpty()) {
            toast(getString(R.string.start_address_empty))
            return
        }
        val endAds = carry_end_address.text.trim().toString()
        if (endAds.isNullOrEmpty()) {
            toast(getString(R.string.end_address_empty))
            return
        }

        val goOffTime = carry_go_off.text.trim()
        if (goOffTime.isNullOrEmpty()) {
            toast(getString(R.string.go_off_empty))
            return
        }

        val peopleCount = carry_people_count.text.trim().toString()
        if (peopleCount.isNullOrEmpty()) {
            toast(getString(R.string.riding_count_empty))
            return
        }

        val price = carry_price.text.trim().toString()
        if (price.isNullOrEmpty()) {
            toast(getString(R.string.price_empty))
            return
        }

        val phone = carry_phone.text.trim().toString()
        if (phone.isNullOrEmpty()) {
            toast(getString(R.string.phone_empty))
            return
        }

        var carryInfo = CarryInfo()
        carryInfo.goOffTime = goOffTime.toString()
        carryInfo.peopleCount = peopleCount.toInt()
        carryInfo.price = price.toInt()
        carryInfo.phone = phone
        carryInfo.mark = carry_mark.text.toString()
        carryInfo.startAddress = startAds
        carryInfo.endAddress = endAds

        saveAddress(startAds, endAds)
        saveCarryInfo(carryInfo)
    }

    fun saveAddress(startAds: String, endAds: String) {
        var bmobHelper = BmobHelper(this)
        bmobHelper.saveAddress(startAds, 1)
        bmobHelper.saveAddress(endAds, 1)
    }

    fun saveCarryInfo(carryInfo: CarryInfo) {
        startProgressDialog()
        if (objectId == null) {
            carryInfo.saveData(object : SaveDataListener {
                override fun onSucceed(objectId: String) {
                    toast(getString(R.string.carry_add_succeed))
                    setResult(1001)
                    finish()
                }
            })
        } else {
            carryInfo.objectId = objectId
            carryInfo.updateData(object : UpdateDataListener {
                override fun onSucceed() {
                    toast(getString(R.string.carry_update_succeed))
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