package cool.eye.ridding.ui

import android.content.Intent
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
import cool.eye.ridding.data.Address
import kotlinx.android.synthetic.main.activity_address.*
import kotlinx.android.synthetic.main.address_item.view.*
import kotlinx.android.synthetic.main.common_title.*

/**
 * Created by cool on 17-1-22.
 */
class AddressActivity : BaseActivity() {

    lateinit var view: View
    lateinit var refreshView: EmptyRecyclerView<Address>

    companion object {
        const val REQUEST_CODE = 1001
        const val ADDRESS = "address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        iv_back.setOnClickListener { finish() }
        tv_title.text = getString(R.string.usual_address)
        refreshView = address_refreshview as EmptyRecyclerView<Address>
        refreshView.setAdapter(AddressAdapter(refreshView.dataList))
        refreshView.setEmptyView(EmptyView(this))
        getAddress()
    }

    fun getAddress() {
        startProgressDialog()
        var query = BmobQuery<Address>()
        query.addWhereEqualTo("userId", BmobUser.getCurrentUser().objectId)
        query.setLimit(20)
        query.order("-carryCount,-ridingCount")
        query.findObjects(object : FindListener<Address>() {
            override fun done(p0: MutableList<Address>?, p1: BmobException?) {
                if (isFinishing) return
                stopProgressDialog()
                if (p0?.isNotEmpty() ?: false) {
                    refreshView.onLoadData(p0!!)
                }
            }
        })
    }

    inner class AddressAdapter(var addressList: MutableList<Address>) : RecyclerView.Adapter<AddressHolder>() {
        override fun getItemCount(): Int {
            return addressList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
            var view = LayoutInflater.from(this@AddressActivity).inflate(R.layout.address_item, parent, false)
            return AddressHolder(view)
        }

        override fun onBindViewHolder(holder: AddressHolder, position: Int) {
            var address = addressList[position]
            holder.view.address_name.text = address.name
            holder.view.address_carry_count.text = "发车${address.carryCount}次"
            holder.view.address_riding_count.text = "乘坐${address.ridingCount}次"
            holder.view.setOnClickListener {
                var intent = Intent()
                intent.putExtra(ADDRESS, addressList[position].name!!)
                setResult(REQUEST_CODE, intent)
                finish()
            }
        }
    }

    inner class AddressHolder(var view: View) : RecyclerView.ViewHolder(view)
}