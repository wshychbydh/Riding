package cool.eye.ridding.zone.contacts

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
import cool.eye.ridding.data.BlackList
import cool.eye.ridding.data.SEX
import cool.eye.ridding.db.DBHelper
import cool.eye.ridding.ui.BaseFragment
import cool.eye.ridding.ui.EmptyRecyclerView
import cool.eye.ridding.ui.EmptyView
import cool.eye.ridding.util.Utils
import kotlinx.android.synthetic.main.black_list_item.view.*
import kotlinx.android.synthetic.main.empty_recyclerview.view.*

/**
 * Created by cool on 17-2-6.
 */
class BlackListFragment : BaseFragment() {

    lateinit var refreshView: EmptyRecyclerView<BlackList>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.empty_recyclerview, container, false)
        refreshView = view.empty_recyclerview as EmptyRecyclerView<BlackList>
        refreshView.setEmptyView(EmptyView(activity))
        refreshView.setAdapter(BlackListAdapter(refreshView.dataList))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Utils.isNetAvaiable(requireContext())) {
            getBlackList()
        } else {
            var result = kotlin.run {
                DBHelper.get(requireContext()).getShareBlackList()
            }
            refreshView.onLoadData(result)
        }
    }

    private fun getBlackList() {
        startProgressDialog()
        var query = BmobQuery<BlackList>()
        query.order("-signCount,updatedAt")
        query.findObjects(object : FindListener<BlackList>() {
            override fun done(p0: MutableList<BlackList>?, p1: BmobException?) {
                if (context == null) return
                if (p0?.isNotEmpty() == true) {
                    Thread(Runnable {
                        DBHelper.get(requireContext()).saveShareBlackList(p0!!)
                    }).start()
                    refreshView.onLoadData(p0!!)
                }
                stopProgressDialog()
            }
        })
    }

    inner class BlackListAdapter(var blackList: MutableList<BlackList>) : RecyclerView.Adapter<PassengerHolder>() {

        override fun getItemCount(): Int {
            return blackList.size
        }

        override fun onBindViewHolder(holder: PassengerHolder, position: Int) {
            var blackList = blackList[position]
            holder.view.blackList_name.text = blackList.name
            holder.view.blackList_count.text = Utils.formatPartColorOfStr(resources.getColor(R.color.orange), getString(R.string.blacklist_sign_count, blackList.signCount), blackList.signCount)
            holder.view.blackList_phone.text = blackList.phone
            holder.view.blackList_sex.text = SEX.valueOfCode(blackList.sex).value
            holder.view.blackList_job.text = blackList.job
            var index = blackList.signUser?.indexOf(BmobUser.getCurrentUser().objectId) ?: -1
            holder.view.blackList_remark.text = blackList.remark?.get(if (index > -1) index else 0)
            holder.view.blackList_remark.visibility = if (blackList.remark?.isEmpty() ?: false) View.GONE else View.VISIBLE
            holder.view.setOnLongClickListener {
                Utils.callPhone(requireContext(), blackList.phone)
                false
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerHolder {
            val view = LayoutInflater.from(activity).inflate(R.layout.black_list_item, parent, false)
            return PassengerHolder(view)
        }

    }

    open inner class PassengerHolder(var view: View) : RecyclerView.ViewHolder(view)
}