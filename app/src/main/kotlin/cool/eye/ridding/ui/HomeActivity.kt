package cool.eye.ridding.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.SparseArray
import android.widget.RadioButton
import cool.eye.ridding.R
import cool.eye.ridding.broadcast.GuardService
import cool.eye.ridding.broadcast.ServiceUtil
import kotlinx.android.synthetic.main.activity_riding.*


class HomeActivity : BaseActivity() {

    private var fragments: SparseArray<Fragment> = SparseArray()
    private var oldFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ServiceUtil.startService(this, GuardService::class.java)
        initView(0)
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        resetView(intent?.getIntExtra(SELECTION, 0) ?: 0)
//    }
//
//    companion object {
//        const val SELECTION = "selection"
//        fun launch(activity: Activity, selection: Int) {
//            reload(activity, selection)
//            activity.finish()
//        }
//
//        fun reload(context: Context, selection: Int) {
//            var intent = Intent(context, HomeActivity::class.java)
//            intent.putExtra(SELECTION, selection)
//            context.startActivity(intent)
//        }
//    }

    fun initView(selection: Int) {
        radiogroup.setOnCheckedChangeListener { radioGroup, i ->
            var index = radioGroup.indexOfChild(radioGroup.findViewById(i))
            var fragment = fragments.get(index)
            if (fragment == null) {
                fragment = when (index) {
                    0 -> RidingFragment()
                    1 -> CarryFragment()
                    2 -> UserZoneFragment()
                    else -> {
                        RidingFragment()
                    }
                }
                fragments.put(index, fragment)
            }
            addFragment(fragment)
        }
        (radiogroup.getChildAt(selection) as RadioButton).isChecked = true
    }

    fun updateCarryHistoryView() {
        val fragment = fragments.get(1)
        if (fragment != null) {
            (fragment as CarryFragment).loadCarryInfo()
        }
    }

//    private fun resetView(selection: Int) {
//        radiogroup.setOnCheckedChangeListener(null)
//        radiogroup.clearCheck()
//        oldFragment = null
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        var fragment: Fragment
//        for (i in 0..fragments.size() - 1) {
//            fragment = fragments.valueAt(i)
//            if (fragment != null && fragment.isAdded) {
//                fragmentTransaction.remove(fragment)
//            }
//        }
//        fragmentTransaction.commitAllowingStateLoss()
//        fragments.clear()
//        initView(selection)
//    }

    private fun addFragment(newFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        if (newFragment.isAdded) {
            transaction.show(newFragment)
        } else {
            transaction.add(R.id.container, newFragment)
        }
        if (oldFragment != null) {
            transaction.hide(oldFragment)
        }
        transaction.commitAllowingStateLoss()
        oldFragment = newFragment
    }
}
