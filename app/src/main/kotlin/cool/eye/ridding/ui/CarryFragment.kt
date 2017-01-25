package cool.eye.ridding.ui

import android.os.Bundle
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
import kotlinx.android.synthetic.main.carry_item.view.*
import kotlinx.android.synthetic.main.fragment_carry.*


/**
 * Created by cool on 17-1-12.
 */
class CarryFragment : BaseFragment() {

    lateinit var refreshView: RefreshViewGroup<CarryInfo>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater?.inflate(R.layout.fragment_carry, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshView = carry_refreshview as RefreshViewGroup<CarryInfo>
        refreshView.setAdapter(RecyclerAdapter(refreshView.dataList))
        refreshView.setEmptyView(EmptyView(context))
        refreshView.onRefresh = {
            //TODO 暂时不需要刷新功能
            refreshView.onLoadComplete()
        }
        getCarryInfo()
    }

    private fun getCarryInfo() {
        startProgressDialog()
        var query = BmobQuery<CarryInfo>()
        query.addWhereEqualTo("status", 1)
        query.addWhereEqualTo("userId", BmobUser.getCurrentUser().objectId)
        query.findObjects(object : FindListener<CarryInfo>() {
            override fun done(p0: MutableList<CarryInfo>?, p1: BmobException?) {
                if (p1 == null) {
                    if (p0?.isNotEmpty() ?: false) {
                        refreshView.onLoadData(p0!!)
                    }
                    refreshView.onLoadComplete()
                } else {
                    toast(p1.message ?: "")
                }
                stopProgressDialog()
            }
        })
    }


    inner class RecyclerAdapter(var carryInfoList: MutableList<CarryInfo>) : RecyclerView.Adapter<CarryViewHolder>() {
        override fun getItemCount(): Int {
            return carryInfoList.size
        }

        override fun onBindViewHolder(holder: CarryViewHolder, position: Int) {
            var carryInfo = carryInfoList[position]
            holder.view.carry_start_end_address.text = carryInfo.composeAddress()
            holder.view.carry_phone.text = carryInfo.phone
            holder.view.carry_price.text = carryInfo.composePrice()
            holder.view.carry_time.text = carryInfo.goOffTime
            holder.view.carry_people_count.text = carryInfo.composeCount()
            if (carryInfo.mark == null) {
                holder.view.carry_mark.visibility = View.GONE
            } else {
                holder.view.carry_mark.visibility = View.GONE
                holder.view.carry_mark.text = carryInfo.mark
            }
            holder.view.id = position
            holder.view.setOnClickListener(onItemClicked)
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CarryViewHolder {
            return CarryViewHolder(LayoutInflater.from(context).inflate(R.layout.carry_item, parent,false))
        }

        var onItemClicked = View.OnClickListener { v ->
            CarryDetailActivity.launch(context, carryInfoList[v.id])
        }

    }

    class CarryViewHolder(var view: View) : RecyclerView.ViewHolder(view)
}