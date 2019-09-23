package cool.eye.ridding.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import cool.eye.ridding.R

/**
 * Created by cool on 17-1-22.
 */
class EmptyView : FrameLayout {
    constructor(context: Context?) : super(context) {
        createView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        createView()
    }

    fun createView() {
        LayoutInflater.from(context).inflate(R.layout.empty_view, this, true)
    }
}