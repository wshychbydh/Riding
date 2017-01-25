package cool.eye.ridding.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by cool on 17-1-18.
 */
class PagerAdapter(var activity: FragmentActivity) : FragmentPagerAdapter(activity.supportFragmentManager) {

    val tabInfos: MutableList<TabInfo> = mutableListOf()

    class TabInfo(internal var _clzz: Class<*>, internal var _args: Bundle?) {
        internal var fragment: Fragment? = null
    }

    fun addTab(_clss: Class<*>, _args: Bundle?) {
        val info = TabInfo(_clss, _args)
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
}