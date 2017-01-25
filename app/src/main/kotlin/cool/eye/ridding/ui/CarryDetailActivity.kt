package cool.eye.ridding.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cool.eye.ridding.R
import cool.eye.ridding.data.CarryInfo
import cool.eye.ridding.data.Riding
import cool.eye.ridding.util.Utils
import kotlinx.android.synthetic.main.activity_carry_detail.*
import kotlinx.android.synthetic.main.common_title.*
import kotlinx.android.synthetic.main.riding_item.view.*

/**
 * Created by cool on 16-11-25.
 */
class CarryDetailActivity : BaseActivity() {

    lateinit var carryInfo: CarryInfo

    companion object {
        const val CARRY_INFO = "carry_info"

        fun launch(context: Context, carryInfo: CarryInfo) {
            var intent = Intent(context, CarryDetailActivity::class.java)
            intent.putExtra(CARRY_INFO, carryInfo)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carry_detail)
        tv_title.text = "载客明细"
        iv_back.setOnClickListener { finish() }
        carryInfo = intent.getSerializableExtra(CARRY_INFO) as CarryInfo
        showCarryInfo()
        carry_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        carry_recyclerview.itemAnimator = DefaultItemAnimator()
        carry_recyclerview.addItemDecoration(DividerDecoration(this))
        startProgressDialog()
        var query = BmobQuery<Riding>()
        query.addWhereEqualTo("carryId", carryInfo.objectId)
        query.findObjects(object : FindListener<Riding>() {
            override fun done(p0: MutableList<Riding>?, p1: BmobException?) {
                stopProgressDialog()
                if (p1 == null && p0?.isNotEmpty() ?: false) {
                    carry_empty.visibility = View.GONE
                    carry_recyclerview.adapter = RidingAdapter(p0!!)
                } else {
                    carry_empty.visibility = View.VISIBLE
                }
                updatePeopleCount(p0)
            }
        })
    }

    fun showCarryInfo(){
        carry_layout.visibility = View.VISIBLE
        carry_address.text = carryInfo.composeAddress()
        carry_time.text = carryInfo.goOffTime
        carry_price.text = carryInfo.composePrice()
        carry_count.text = carryInfo.composeCount()
        carry_phone.text = carryInfo.phone
        carry_mark.visibility = if (carryInfo.mark.isNullOrEmpty()) View.GONE else View.VISIBLE
        carry_mark.text = carryInfo.mark
    }

    fun updatePeopleCount(ridingList: MutableList<Riding>?) {
        var peopleCount = 0
        ridingList?.forEach { riding -> peopleCount + riding.peopleCount }
        var content1 = "预载${carryInfo.peopleCount}人"
        var content2 = "实载${peopleCount}人"
        var color = resources.getColor(R.color.yellow)
        people_total.text = "${Utils.formatColorOfStr(content1, color, 2, 3)},${Utils.formatColorOfStr(content2, color, 2, 3)}"
    }

    inner class RidingAdapter(var ridingList: MutableList<Riding>) : RecyclerView.Adapter<RidingHolder>() {

        override fun getItemCount(): Int {
            return ridingList.size
        }

        override fun onBindViewHolder(holder: RidingHolder, position: Int) {
            holder.view.riding_item_name.text = ridingList[position].passenger?.name
            holder.view.riding_item_count.text = "${ridingList[position].peopleCount()}"
            holder.view.riding_item_phone.text = ridingList[position].passenger?.phone
            holder.view.riding_item_address.text = "${ridingList[position].composeAddress()}"
            holder.view.riding_item_time.text = Utils.formatSimpleTime(ridingList[position].ridingTime)
            holder.view.riding_item_call.setOnClickListener {
                Utils.callPhone(baseContext, ridingList[position].passenger!!.phone)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RidingHolder {
            val view = LayoutInflater.from(baseContext).inflate(R.layout.riding_item, null)
            return RidingHolder(view)
        }

    }

    open inner class RidingHolder(var view: View) : RecyclerView.ViewHolder(view)
}