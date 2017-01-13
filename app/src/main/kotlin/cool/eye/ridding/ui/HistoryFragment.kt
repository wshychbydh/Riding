package cool.eye.ridding.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cool.eye.ridding.R

/**
 * Created by cool on 17-1-12.
 */
class HistoryFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val View = inflater?.inflate(R.layout.fragment_riding, container, false)
        return View
    }
}