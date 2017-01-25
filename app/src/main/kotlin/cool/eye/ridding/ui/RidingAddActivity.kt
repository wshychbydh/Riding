package cool.eye.ridding.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatEditText
import android.widget.ArrayAdapter
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cool.eye.ridding.R
import cool.eye.ridding.data.Address
import cool.eye.ridding.data.CarryInfo
import cool.eye.ridding.data.Passenger
import cool.eye.ridding.data.Riding
import cool.eye.ridding.db.BmobHelper
import cool.eye.ridding.ui.dialog.DatePickerDialog
import cool.eye.ridding.util.SaveDataListener
import kotlinx.android.synthetic.main.activity_add_riding.*
import kotlinx.android.synthetic.main.common_title.*


class RidingAddActivity : BaseActivity() {

    lateinit var peopleCount: Array<String>
    lateinit var carryInfo: CarryInfo
    lateinit var addressEt: AppCompatEditText

    companion object {

        const val REMAIN_COUNT = "remain_count"
        const val CARRY_INFO = "carry_info"

        fun launch(context: Context, remainCount: Int, carryInfo: CarryInfo) {
            var intent = Intent(context, RidingAddActivity::class.java)
            intent.putExtra(REMAIN_COUNT, remainCount)
            intent.putExtra(CARRY_INFO, carryInfo)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_riding)
        carryInfo = intent.getSerializableExtra(CARRY_INFO) as CarryInfo
        peopleCount = Array(intent.getIntExtra(REMAIN_COUNT, 4), { i -> "${i + 1} 人" })
        tv_title.text = getString(R.string.passenger_add)
        iv_back.setOnClickListener { finish() }
        submit.setOnClickListener { submit() }
        ridding_time.setOnClickListener {
            var dialog = DatePickerDialog(this)
            dialog.onDateSelectedListener = {
                time ->
                ridding_time.text = time
            }
            dialog.currentTime = ridding_time.text.toString()
            dialog.show()
        }
        start_address_common.setOnClickListener {
            setAddress(end_address)
        }
        end_address_common.setOnClickListener {
            setAddress(start_address)
        }

        start_address.setTextAndSelectEnd(carryInfo.startAddress!!)
        end_address.setText(carryInfo.endAddress!!)
        ridding_time.text = carryInfo.goOffTime
        price.setText("${carryInfo.price}")
        passenger_name.setSelection(passenger_name.text.length)
        people_count.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, peopleCount)
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
        val name = passenger_name.text.toString()
        if (name.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.name_empty), Toast.LENGTH_SHORT).show()
            return
        }
        val startAds = start_address.text.toString()
        if (startAds.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.start_address_empty), Toast.LENGTH_SHORT).show()
            return
        }
        val endAds = end_address.text.toString()
        if (endAds.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.end_address_empty), Toast.LENGTH_SHORT).show()
            return
        }
        val phone = phone.text.toString()
        if (phone.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.phone_empty), Toast.LENGTH_SHORT).show()
            return
        }

        var startAddress = Address()
        var endAddress = Address()
        startAddress.name = startAds
        startAddress.ridingCount++
        endAddress.name = endAds
        endAddress.ridingCount++

        var passenger = Passenger()
        passenger.name = name
        passenger.phone = phone
        passenger.by_count++

        var data = Riding()
        data.passenger = passenger
        data.goOffTime = carryInfo.goOffTime!!
        data.ridingTime = ridding_time.text.toString()
        data.peopleCount = people_count.selectedItemPosition + 1
        data.startAddress = startAds
        data.endAddress = endAds
        data.mark = mark.text.toString()
        saveAddress(startAddress, endAddress)
        saveData(data)
    }

    fun saveAddress(startAddress: Address, endAddress: Address) {
        var bmobHelper = BmobHelper(this)
        bmobHelper.saveAddress(startAddress)
        bmobHelper.saveAddress(endAddress)
    }

    fun saveData(riding: Riding) {
        startProgressDialog()
        riding.passenger!!.save(object : SaveListener<String?>() {
            override fun done(p0: String?, p1: BmobException?) {
                if (p1 == null) {
                    saveRiding(riding)
                } else {
                    var bmobQuery = BmobQuery<Passenger>()
                    bmobQuery.addWhereEqualTo("phone",riding.passenger!!.phone)
                    bmobQuery.findObjects(object: FindListener<Passenger>() {
                        override fun done(p0: MutableList<Passenger>?, p1: BmobException?) {
                            if (p1 == null) {
                                riding.passenger = p0!![0]
                                saveRiding(riding)
                            } else {
                                toast(p1.message ?: "")
                                stopProgressDialog()
                            }
                        }
                    })
                }
            }
        })
    }

    fun saveRiding(riding: Riding) {
        riding.saveData(object : SaveDataListener {
            override fun onSucceed(objectId: String) {
                stopProgressDialog()
                toast(getString(R.string.passenger_add_succeed))
                HomeActivity.launch(this@RidingAddActivity, 0)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == AddressActivity.REQUEST_CODE && resultCode == AddressActivity.REQUEST_CODE) {
            addressEt.setText(data.getStringExtra(AddressActivity.ADDRESS))
        }
    }
}
