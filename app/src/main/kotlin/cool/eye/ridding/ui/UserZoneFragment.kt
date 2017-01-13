package cool.eye.ridding.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cool.eye.ridding.R
import cool.eye.ridding.ui.scan.CaptureActivity
import cool.eye.ridding.ui.scan.QrEncodeActivity
import kotlinx.android.synthetic.main.fragment_zone.*

/**
 * Created by cool on 17-1-12.
 */
class UserZoneFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val View = inflater?.inflate(R.layout.fragment_zone, container, false)
        return View
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_encode.setOnClickListener { startActivity(Intent(activity, QrEncodeActivity::class.java)) }
        tv_scan.setOnClickListener { startActivity(Intent(activity, CaptureActivity::class.java)) }
    }
}