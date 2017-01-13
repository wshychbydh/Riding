package cool.eye.ridding.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cool.eye.ridding.R
import cool.eye.ridding.util.Utils
import cooleye.eot.kotlin.data.Riding
import kotlinx.android.synthetic.main.activity_riding_detail.*
import kotlinx.android.synthetic.main.common_title.*

class RidingDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riding_detail)
        tv_title.text = "乘客信息"
        iv_back.setOnClickListener { finish() }

        val riding = intent.getSerializableExtra("riding") as? Riding
        if (riding != null) {
            passenger_name.text = riding.passenger.name
            sex.text = riding.passenger.sex.value
            address.text = riding.composeAddress()
            count.text = riding.peopleCount()
            start_time.text = Utils.formatFullTime(riding.ridingTime)
            phone.text = riding.passenger.phone
            mark.text = riding.mark
        }
    }
}
