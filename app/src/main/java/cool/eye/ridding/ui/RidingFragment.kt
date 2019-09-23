package cool.eye.ridding.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cool.eye.ridding.R
import cool.eye.ridding.data.CarryInfo
import cool.eye.ridding.data.CarryStatus
import cool.eye.ridding.data.Riding
import cool.eye.ridding.db.DBHelper
import cool.eye.ridding.util.DeleteDataListener
import cool.eye.ridding.util.UpdateDataListener
import cool.eye.ridding.util.Utils
import kotlinx.android.synthetic.main.fragment_riding.*
import kotlinx.android.synthetic.main.riding_item.view.*

/**
 * Created by cool on 16-11-25.
 */
class RidingFragment : BaseFragment() {

    var remainCount = 4 //剩余可载的人数
    lateinit var carryInfo: CarryInfo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val View = inflater?.inflate(R.layout.fragment_riding, container, false)
        return View
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    fun loadData() {
        startProgressDialog()
        var query = BmobQuery<CarryInfo>()
        query.addWhereEqualTo("status", CarryStatus.UNDERWAY.code)
        query.addWhereEqualTo("userId", BmobUser.getCurrentUser().objectId)
        query.order("-updatedAt,-createdAt")
        query.findObjects(object : FindListener<CarryInfo>() {
            override fun done(p0: MutableList<CarryInfo>?, p1: BmobException?) {
                if (context == null) return
                if (p0?.isNotEmpty() ?: false) {
                    carryInfo = p0!![0]
                    getRidingInfo()
                    showAddPassengerView()
                } else {
                    showAddCarryView()
                }
                stopProgressDialog()
            }
        })
    }

    fun showAddCarryView() {
        carry_layout.visibility = View.GONE
        riding_recyclerview.adapter = null
        riding_recyclerview.removeAllViews()
        carry_finished.visibility = View.GONE
        riding_status_tv.text = getString(R.string.car_submit_none)
        riding_add.visibility = View.VISIBLE
        riding_add.text = getString(R.string.car_submit)
        riding_add.setOnClickListener {
            CarryAddActivity.launch(this@RidingFragment, null)
        }
    }

    fun showAddPassengerView() {
        startProgressDialog()
        carry_layout.visibility = View.VISIBLE
        carry_address.text = carryInfo.composeAddress()
        carry_time.text = carryInfo.goOffTime
        carry_price.text = carryInfo.composePrice()
        carry_count.text = carryInfo.composeCount()
        carry_phone.text = carryInfo.phone
        carry_mark.visibility = if (carryInfo.mark.isNullOrEmpty()) View.GONE else View.VISIBLE
        carry_mark.text = carryInfo.mark
        carry_change.setOnClickListener {
            CarryAddActivity.launch(this@RidingFragment, carryInfo)
        }

        riding_add.text = getString(R.string.passenger_add)
        riding_add.visibility = if (remainCount > 0) View.VISIBLE else View.GONE
        carry_finished.visibility = View.VISIBLE
        carry_finished.setOnClickListener {
            AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.carry_finish_query))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.sure)) { _, _ ->
                        carryInfo.status = CarryStatus.FINISHED.code
                        carryInfo.updateData(object : UpdateDataListener {
                            override fun onSucceed() {
                                showAddCarryView()
                                (activity as HomeActivity).updateCarryHistoryView()
                            }
                        })
                    }.show()
        }
        riding_add.setOnClickListener { RidingAddActivity.launch(this@RidingFragment, remainCount, carryInfo, null) }
        riding_recyclerview.layoutManager = LinearLayoutManager(context)
        riding_recyclerview.itemAnimator = DefaultItemAnimator()
        riding_recyclerview.addItemDecoration(DividerDecoration(context))
    }

    fun getRidingInfo() {
        riding_recyclerview.adapter = null
        riding_recyclerview.removeAllViews()
        var query = BmobQuery<Riding>()
        query.addWhereEqualTo("userId", BmobUser.getCurrentUser().objectId)
        query.addWhereEqualTo("carryId", carryInfo.objectId)
        //   query.addWhereEqualTo("goOffTime", carryInfo.goOffTime)
        query.include("passenger")
        query.findObjects(object : FindListener<Riding>() {
            override fun done(p0: MutableList<Riding>?, p1: BmobException?) {
                if (context == null) return
                stopProgressDialog()
                if (p1 != null) {
                    toast(p1.message ?: "")
                } else {
                    if (p0?.isEmpty() ?: true) {
                        remainCount = 4
                        riding_status_tv.text = getString(R.string.passenger_empty)
                    } else {
                        riding_recyclerview.adapter = RidingAdapter(p0!!)
                        var count = 0
                        p0.forEach { riding -> count += riding.peopleCount }
                        remainCount = carryInfo.peopleCount - count
                        riding_add.visibility = if (remainCount > 0) View.VISIBLE else View.GONE
                        riding_status_tv.text = Utils.formatColorOfStr(getString(R.string.passenger_take_count, count), resources.getColor(R.color.red), 2, 3)
                    }
                }
            }
        })
    }

    inner class RidingAdapter(var ridings: MutableList<Riding>) : RecyclerView.Adapter<RidingHolder>() {

        override fun getItemCount(): Int {
            return ridings?.size
        }

        override fun onBindViewHolder(holder: RidingHolder, position: Int) {
            var passenger = ridings[position].passenger!!
            holder.view.riding_item_name.text = passenger.name
            holder.view.riding_item_count.text = "${ridings[position].peopleCount()}"
            holder.view.riding_item_phone.text = ridings[position].passenger?.phone
            holder.view.riding_item_address.text = "${ridings[position].composeAddress()}"
            holder.view.riding_item_time.text = Utils.formatSimpleTime(ridings[position].ridingTime)
            holder.view.riding_item_mark_layout.visibility = if (ridings[position].mark.isNullOrEmpty()) View.GONE else View.VISIBLE
            holder.view.riding_item_mark.text = ridings[position].mark
            if (passenger.by_count > 1) {
                holder.view.riding_times.visibility = View.VISIBLE
                var realCount = passenger.by_count - 1 //因为在添加乘车记录的时候就默认添加了一次乘车记录
                holder.view.riding_times.text = Utils.formatPartColorOfStr(resources.getColor(R.color.orange),
                        getString(R.string.by_count, realCount), realCount)
            } else {
                holder.view.riding_times.visibility = View.GONE
            }
            holder.view.riding_item_call.setOnClickListener {
                Utils.callPhone(requireContext(), passenger.phone)
            }
            holder.view.setOnClickListener {
                //计算可用剩余人数
                RidingAddActivity.launch(this@RidingFragment, remainCount + ridings[position].peopleCount, carryInfo, ridings[position])
            }
            holder.view.setOnLongClickListener {
                AlertDialog.Builder(requireContext())
                        .setMessage(getString(R.string.select_what_you_do))
                        .setNegativeButton(getString(R.string.blacklist)) { _, _ ->
                            blackList(position)
                        }
                    .setNeutralButton(getString(R.string.cancel), null)
                        .setPositiveButton(getString(R.string.delete)) { _, _ ->
                            deleteRiding(position)
                        }.show()
                false
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RidingHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.riding_item, null)
            return RidingHolder(view)
        }

        fun blackList(position: Int) {
            val passenger = ridings[position].passenger
            passenger!!.promise_not++
            passenger.by_count--
            DBHelper.get(requireContext()).saveBlackList(passenger)
            passenger.updateData(object : UpdateDataListener {
                override fun onSucceed() {
                    toast(getString(R.string.blacklist_add_succeed))
                    deleteRiding(position)
                }
            })
        }

        fun deleteRiding(position: Int) {
            val riding = ridings[position]

            riding.passenger!!.by_count--
            riding.passenger!!.updateData()

            riding.deleteData(riding.objectId, object : DeleteDataListener {
                override fun onSucceed() {
                    toast(getString(R.string.delete_succeed))
                    startProgressDialog()
                    getRidingInfo()
                }
            })
        }
    }

    open inner class RidingHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == 1001) {
            loadData()
        }
    }
}