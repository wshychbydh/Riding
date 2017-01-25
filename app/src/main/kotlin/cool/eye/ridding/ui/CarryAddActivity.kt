package cool.eye.ridding.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatEditText
import android.view.View
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cool.eye.ridding.R
import cool.eye.ridding.data.Address
import cool.eye.ridding.data.CarryInfo
import cool.eye.ridding.db.BmobHelper
import cool.eye.ridding.ui.dialog.DatePickerDialog
import cool.eye.ridding.util.UpdateDataListener
import kotlinx.android.synthetic.main.activity_carry.*

/**
 * Created by cool on 17-1-17.
 */
class CarryAddActivity : BaseActivity() {

    lateinit var addressEt: AppCompatEditText
    var carryInfo: CarryInfo? = null

    companion object {
        const val CARRY_INFO = "carry_info"
        fun launch(context: Context, carryInfo: CarryInfo) {
            var intent = Intent(context, CarryAddActivity::class.java)
            intent.putExtra(CARRY_INFO, carryInfo)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carry)
        carryInfo = intent.getSerializableExtra(CARRY_INFO) as CarryInfo?

        if (carryInfo != null) {
            carry_start_address.setText(carryInfo!!.startAddress)
            carry_end_address.setText(carryInfo!!.endAddress)
            carry_go_off.text = carryInfo!!.goOffTime
            carry_people_count.setText(carryInfo!!.peopleCount.toString())
            carry_price.setText(carryInfo!!.price.toString())
            carry_phone.setText(carryInfo!!.phone)
            carry_mark.setText(carryInfo!!.mark)
        }

        carry_go_off.setOnClickListener {
            var dialog = DatePickerDialog(this)
            dialog.onDateSelectedListener = {
                time ->
                carry_go_off.text = time
            }
            dialog.currentTime = carry_go_off.text.toString()
            dialog.show()
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
        val startAds = carry_start_address.text.toString()
        if (startAds.isNullOrEmpty()) {
            toast(getString(R.string.start_address_empty))
            return
        }
        val endAds = carry_end_address.text.toString()
        if (endAds.isNullOrEmpty()) {
            toast(getString(R.string.end_address_empty))
            return
        }

        val goOffTime = carry_go_off.text
        if (goOffTime.isNullOrEmpty()) {
            toast(getString(R.string.go_off_empty))
            return
        }

        val peopleCount = carry_people_count.text.toString()
        if (peopleCount.isNullOrEmpty()) {
            toast(getString(R.string.riding_count_empty))
            return
        }

        val price = carry_price.text.toString()
        if (price.isNullOrEmpty()) {
            toast(getString(R.string.price_empty))
            return
        }

        val phone = carry_phone.text.toString()
        if (phone.isNullOrEmpty()) {
            toast(getString(R.string.phone_empty))
            return
        }

        var startAddress = Address()
        var endAddress = Address()
        startAddress.name = startAds
        startAddress.carryCount++
        endAddress.name = endAds
        endAddress.carryCount++
        carryInfo = CarryInfo()
        carryInfo!!.goOffTime = goOffTime.toString()
        carryInfo!!.peopleCount = peopleCount.toInt()
        carryInfo!!.price = price.toInt()
        carryInfo!!.phone = phone
        carryInfo!!.mark = carry_mark.text.toString()
        carryInfo!!.startAddress = startAds
        carryInfo!!.endAddress = endAds

        saveAddress(startAddress, endAddress)
        saveData(carryInfo!!)
    }

    fun saveAddress(startAddress: Address, endAddress: Address) {
        var bmobHelper = BmobHelper(this)
        bmobHelper.saveAddress(startAddress)
        bmobHelper.saveAddress(endAddress)
    }

    fun saveData(carryInfo: CarryInfo) {
        startProgressDialog()
        if (carryInfo.objectId == null) {
            carryInfo.save(object : SaveListener<String?>() {
                override fun done(objectId: String?, p1: BmobException?) {
                    stopProgressDialog()
                    if (p1 == null) {
                        toast(getString(R.string.carry_add_succeed))
                        HomeActivity.launch(this@CarryAddActivity, 0)
                    } else {
                        toast(p1.message ?: getString(R.string.unkonw_error))
                    }
                }
            })
        } else {
            carryInfo.updateData(object : UpdateDataListener {
                override fun onSucceed() {
                    toast(getString(R.string.carry_update_succeed))
                    HomeActivity.launch(this@CarryAddActivity, 0)
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