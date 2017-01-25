package cool.eye.ridding.ui

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import cool.eye.ridding.R

/**
 * Created by cool on 16-7-14.
 * 下拉刷新，上拉加载更多
 */
class RefreshViewGroup<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs), SwipeRefreshLayout.OnRefreshListener {

    private val refreshLayout: SwipeRefreshLayout = SwipeRefreshLayout(getContext())
    private val recyclerView: RecyclerView = RecyclerView(getContext())
    private val layoutManager: LinearLayoutManager = LinearLayoutManager(getContext())
    private var isLoadingMore = false
    private var mutableDataList: MutableList<T>? = null
    private var emptyView: View? = null

    var onRefresh: (() -> Unit)? = null
    var onLoadMore: (() -> Unit)? = null

    init {
        refreshLayout.setColorSchemeResources(R.color.colorPrimary)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerDecoration(context))
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.addView(recyclerView)
        addView(refreshLayout)
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

    override fun onRefresh() {
        onRefresh?.invoke()
    }

    fun notifyDataSetChanged() {
        recyclerView.adapter?.notifyDataSetChanged()
    }

    fun onLoadData(data: MutableList<T>) {
        if (mutableDataList == null) {
            dataList
        }
        val loadMoreAble = data.size === PAGE_SIZE
        if (loadMoreAble) {
            recyclerView.addOnScrollListener(mOnScrollListener)
        } else {
            recyclerView.removeOnScrollListener(mOnScrollListener)
        }
        if (!isLoadingMore) {
            mutableDataList!!.clear()
        }
        mutableDataList!!.addAll(data)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    fun onLoadComplete() {
        isLoadingMore = false
        refreshLayout.isRefreshing = false
        emptyView?.visibility = if (mutableDataList == null || mutableDataList!!.isEmpty()) VISIBLE else GONE
    }

    private val mOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            val totalItemCount = layoutManager.itemCount
            if (lastVisibleItem >= totalItemCount - LOAD_MORE_LAST_COUNT && dy > 0) {
                if (!isLoadingMore) {
                    if (onLoadMore != null) {
                        isLoadingMore = true
                        onLoadMore!!.invoke()
                    }
                }
            }
        }
    }

    companion object {

        val PAGE_SIZE = 24 //每次请求返回的数量
        private val LOAD_MORE_LAST_COUNT = 1 //剩下1个item后自动加载
    }
}
