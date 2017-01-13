package cool.eye.ridding.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import cool.eye.ridding.R
import cool.eye.ridding.util.Utils
import cooleye.eot.kotlin.data.Passenger
import cooleye.eot.kotlin.data.Riding
import cooleye.eot.kotlin.db.DbHelper
import kotlinx.android.synthetic.main.activity_add_riding.*
import kotlinx.android.synthetic.main.common_title.*

class RidingAddActivity : AppCompatActivity() {

    lateinit var peopleCount: Array<Int>
    val ADDRESS = listOf("金融城", "火车东站", "威远县委")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_riding)
        var lastCount = DbHelper.getRidingCount()

        peopleCount = Array(4 - lastCount, { i -> i + 1 })
        tv_title.text = "添加乘客"
        iv_back.setOnClickListener { finish() }
        submit.setOnClickListener { submit() }
        start_time.setOnClickListener {
            var dialog = RidingDatePicker(this)
            dialog.onDateSelectedListener = {
                time ->
                start_time.text = time
            }
            dialog.show()
        }
        passenger_name.setSelection(passenger_name.text.length)
        start_time.text = Utils.formatFullTime(System.currentTimeMillis())
        people_count.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, peopleCount)
        start_address.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ADDRESS)
        end_address.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ADDRESS.reversed())
    }

    fun submit() {
        val name = passenger_name.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(this, "名字不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        val startAds = start_address.selectedItem
        if (startAds == null) {
            Toast.makeText(this, "出发地址不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        val endAds = end_address.selectedItem
        if (endAds == null) {
            Toast.makeText(this, "目的地址不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        val phone = phone.text.toString()
        if (phone.isEmpty()) {
            Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        val data = Riding(passenger = Passenger(name, phone = phone),
                peopleCount = people_count.selectedItem as Int,
                ridingTime = Utils.getTime(start_time.text.toString()),
                startAddress = startAds as String,
                endAddress = endAds as String,
                mark = mark.text.toString(),
                startCity = null,
                endCity = null,
                startLatitude = null,
                startLongitude = null)
        var result = DbHelper.saveRidingInfo(data)
        var msg = if (result) "保存成功" else "保存失败"
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        startActivity(Intent(this, RidingActivity::class.java))
        finish()
    }
}
