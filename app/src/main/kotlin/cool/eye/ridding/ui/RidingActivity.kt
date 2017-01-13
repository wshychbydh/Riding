package cool.eye.ridding.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.RadioButton
import android.widget.Toast
import cool.eye.ridding.R
import kotlinx.android.synthetic.main.activity_riding.*


class RidingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Toast.makeText(this, "onNewIntent", Toast.LENGTH_LONG).show()
        initView()
    }

    fun initView(){
        viewpager.adapter = null
        viewpager.removeAllViews()
        var adapter = ViewPagerAdapter()
        adapter.addTab(RidingFragment::class.java, null)
        adapter.addTab(HistoryFragment::class.java, null)
        adapter.addTab(UserZoneFragment::class.java, null)
        viewpager.adapter = adapter
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                (radiogroup.getChildAt(position)!! as RadioButton).isChecked = true
            }
        })
        radiogroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            viewpager.currentItem = radioGroup!!.indexOfChild(radioGroup.findViewById(checkedId))
        }
    }

    inner class ViewPagerAdapter : FragmentPagerAdapter(supportFragmentManager) {

        val tabInfos: MutableList<TabInfo> = mutableListOf()

        inner class TabInfo(internal var _clss: Class<*>, internal var _args: Bundle?) {
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
                info.fragment = Fragment.instantiate(baseContext,
                        info._clss.name, info._args)

            return info.fragment!!
        }
    }
}
