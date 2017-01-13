package cool.eye.ridding.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cool.eye.ridding.R
import cool.eye.ridding.util.Utils
import cooleye.eot.kotlin.data.Riding
import cooleye.eot.kotlin.db.DbHelper
import kotlinx.android.synthetic.main.fragment_riding.*
import kotlinx.android.synthetic.main.riding_item.view.*
import java.util.*

/**
 * Created by cool on 16-11-25.
 */
class RidingFragment : Fragment() {

    var ridings: ArrayList<Riding>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val View = inflater?.inflate(R.layout.fragment_riding, container, false)
        return View
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        riding_add.visibility = if (DbHelper.getRidingCount() >= 4) View.GONE else View.VISIBLE
        riding_add.setOnClickListener { startActivity(Intent(activity, RidingAddActivity::class.java)) }
        riding_recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        riding_recyclerview.itemAnimator = DefaultItemAnimator()
        riding_recyclerview.addItemDecoration(DividerDecoration(context))
        ridings = DbHelper.getRidings()
        riding_recyclerview.adapter = RidingAdapter()
        updatePeopleCount()
    }

    fun updatePeopleCount(){
        var count = 0
        ridings?.forEach { riding -> count += riding.peopleCount }
        people_total.text = "$count 人"
    }

    inner class RidingAdapter : RecyclerView.Adapter<RidingHolder>() {

        override fun getItemCount(): Int {
            return ridings?.size ?: 0
        }

        override fun onBindViewHolder(holder: RidingHolder, position: Int) {
            holder.view.name.text = ridings!![position].passenger.name
            holder.view.count.text = "${ridings!![position].peopleCount()}"
            holder.view.phone.text = ridings!![position].passenger.phone
            holder.view.address.text = "${ridings!![position].composeAddress()}"
            holder.view.time.text = Utils.formatSimpleTime(ridings!![position].ridingTime)
            holder.view.setOnClickListener {
                val intent = Intent(activity, RidingDetailActivity::class.java)
                intent.putExtra("riding", ridings!![position])
                startActivity(intent)
            }

            holder.view.call.setOnClickListener {
                Utils.callPhone(context, ridings!![position].passenger.phone)
            }
            holder.view.setOnLongClickListener {
                AlertDialog.Builder(context)
                        .setMessage("ｑ确定要删除该条记录吗?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", { dialog, witch ->
                            DbHelper.deleteRidingInfo(ridings!![position].passenger.phone)
                            ridings!!.removeAt(position)
                            riding_recyclerview.adapter.notifyDataSetChanged()
                            updatePeopleCount()
                        }).show()
                false
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RidingHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.riding_item, null)
            return RidingHolder(view)
        }
    }

    open inner class RidingHolder(var view: View) : RecyclerView.ViewHolder(view)
}