package cool.eye.ridding.ui

import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * Created by cool on 16-7-14.
 * 下拉刷新，上拉加载更多
 */
class EmptyRecyclerView<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs){

    private val recyclerView: RecyclerView = RecyclerView(getContext())
    private val layoutManager: LinearLayoutManager = LinearLayoutManager(getContext())
    private var mutableDataList: MutableList<T>? = null
    private var emptyView: View? = null

    var onRefresh: (() -> Unit)? = null

    init {
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerDecoration(context))
        addView(recyclerView)
    }

    /**
     * 当数据为空时显示的内容，不设置就不会显示
     * @param emptyView
     */
    fun setEmptyView(emptyView: View) {
        if (this.emptyView != null) {
            removeView(this.emptyView)
        }
        this.emptyView = emptyView
        addView(this.emptyView, 0)
    }

    val dataList: MutableList<T>
        get() {
            if (mutableDataList == null) {
                mutableDataList = mutableListOf()
            }
            return mutableDataList!!
        }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
    }

    fun notifyDataSetChanged() {
        recyclerView.adapter?.notifyDataSetChanged()
    }

    fun onLoadData(data: List<T>) {
        if (mutableDataList == null) {
            dataList
        }
        mutableDataList!!.addAll(data)
        recyclerView.adapter?.notifyDataSetChanged()
        emptyView?.visibility = if (mutableDataList == null || mutableDataList!!.isEmpty()) VISIBLE else GONE
    }
}
