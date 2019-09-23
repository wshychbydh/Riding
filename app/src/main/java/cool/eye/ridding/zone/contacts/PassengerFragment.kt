package cool.eye.ridding.zone.contacts

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import cool.eye.ridding.R
import cool.eye.ridding.data.Passenger
import cool.eye.ridding.data.SEX
import cool.eye.ridding.db.DBHelper
import cool.eye.ridding.ui.BaseFragment
import cool.eye.ridding.ui.EmptyRecyclerView
import cool.eye.ridding.ui.EmptyView
import cool.eye.ridding.util.Utils
import kotlinx.android.synthetic.main.empty_recyclerview.view.*
import kotlinx.android.synthetic.main.passenger_item.view.*

/**
 * Created by cool on 17-2-6.
 */
class PassengerFragment : BaseFragment() {

    lateinit var refreshView: EmptyRecyclerView<Passenger>

    companion object {
        const val PASSENGER_TYPE = "passenger_type"
        const val PASSENGER = 0
        const val BLACK_LIST = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.empty_recyclerview, container, false)
        refreshView = view.empty_recyclerview as EmptyRecyclerView<Passenger>
        refreshView.setEmptyView(EmptyView(activity))
        refreshView.setAdapter(PassengerAdapter(refreshView.dataList))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var type = arguments?.getInt(PASSENGER_TYPE)
        if (Utils.isNetAvaiable(requireContext())) {
            getPassengers(type)
        } else {
            var result = kotlin.run {
                if (type == BLACK_LIST) DBHelper.get(requireContext()).getBlackList(BmobUser.getCurrentUser().objectId)
                else DBHelper.get(requireContext()).getPassengers(BmobUser.getCurrentUser().objectId)
            }
            refreshView.onLoadData(result)
        }
    }


    private fun getPassengers(type: Int?) {
        startProgressDialog()
        var query = BmobQuery<Passenger>()
        query.addWhereEqualTo("userId", BmobUser.getCurrentUser().objectId)
        if (type == BLACK_LIST) {
            query.addWhereGreaterThan("promise_not", 0)
        } else {
            query.addWhereEqualTo("promise_not", 0)
        }
        query.order("-by_count,-promise_not,updatedAt")
        query.findObjects(object : FindListener<Passenger>() {
            override fun done(p0: MutableList<Passenger>?, p1: BmobException?) {
                if (context == null) return
                if (p0?.isNotEmpty() == true) {
                    Thread(Runnable {
                        if (type == BLACK_LIST) {
                            DBHelper.get(requireContext()).saveBlackList(p0!!)
                        } else {
                            DBHelper.get(requireContext()).savePassengers(p0!!)
                        }
                    }).start()
                    refreshView?.onLoadData(p0!!)
                }
                stopProgressDialog()
            }
        })
    }

    inner class PassengerAdapter(var passengers: MutableList<Passenger>) : RecyclerView.Adapter<PassengerHolder>() {

        override fun getItemCount(): Int {
            return passengers.size
        }

        override fun onBindViewHolder(holder: PassengerHolder, position: Int) {
            var passenger = passengers[position]
            holder.view.passenger_name.text = passenger.name
            holder.view.passenger_count.text = Utils.formatPartColorOfStr(resources.getColor(R.color.orange), getString(R.string.by_count, passenger.by_count), passenger.by_count)
            holder.view.passenger_phone.text = passenger.phone
            holder.view.passenger_sex.text = SEX.valueOfCode(passenger.sex).value
            holder.view.passenger_job.text = passenger.job
            holder.view.passenger_remark.text = passenger.remark
            holder.view.passenger_remark.visibility = if (passenger.remark.isNullOrEmpty()) View.GONE else View.VISIBLE

            holder.view.passenger_item_call.setOnClickListener {
                Utils.callPhone(requireContext(), passenger.phone)
            }

            holder.view.setOnClickListener {
                PassengerAddActivity.launch(requireActivity(), passenger)
            }
            holder.view.setOnLongClickListener {
                AlertDialog.Builder(requireContext()).setMessage("确定要删除联系人吗?")
                        .setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                          passenger.delete(object : UpdateListener() {
                            override fun done(p0: BmobException?) {
                              if (p0 != null) {
                                toast(getString(R.string.delete_succeed))
                                passengers.remove(passenger)
                                notifyDataSetChanged()
                              }
                            }
                          })
                        }
                    .setNegativeButton(resources.getString(R.string.cancel), null)
                        .show()
                false
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerHolder {
            val view = LayoutInflater.from(activity).inflate(R.layout.passenger_item, parent, false)
            return PassengerHolder(view)
        }

    }

    open inner class PassengerHolder(var view: View) : RecyclerView.ViewHolder(view)
}