package cool.eye.ridding.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by cool on 17-1-18.
 */
class FragmentTablePagerAdapter(var activity: FragmentActivity) : FragmentPagerAdapter(activity.supportFragmentManager) {

    val tabInfos: MutableList<TabInfo> = mutableListOf()

    class TabInfo(internal var _clzz: Class<*>, internal var _args: Bundle?) {
        internal var fragment: Fragment? = null
        internal var title: CharSequence? = null
    }

    fun addTab(_clss: Class<*>, _args: Bundle?) {
        val info = TabInfo(_clss, _args)
        tabInfos.add(info)
        notifyDataSetChanged()
    }

    fun addTab(_clss: Class<*>, _args: Bundle?, title: CharSequence) {
        val info = TabInfo(_clss, _args)
        info.title = title
        tabInfos.add(info)
        notifyDataSetChanged()
    }

    fun clear() {
        tabInfos.clear()
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return tabInfos.size
    }

    override fun getItem(position: Int): Fragment {
        val info = tabInfos[position]
        if (info.fragment == null)
            info.fragment = Fragment.instantiate(activity, info._clzz.name, info._args)

        return info.fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabInfos[position].title ?: super.getPageTitle(position)
    }
}