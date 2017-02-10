package cool.eye.ridding.zone.card.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cool.eye.ridding.R
import cool.eye.ridding.adapter.FragmentTablePagerAdapter
import kotlinx.android.synthetic.main.activity_card.*
import kotlinx.android.synthetic.main.common_title.*

class CardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        iv_back.setOnClickListener { finish() }
        iv_submit.setImageResource(R.drawable.add)
        iv_submit.setOnClickListener { startActivityForResult(Intent(this, CardAddActivity::class.java), 1001) }
        loadView()
    }

    fun loadView() {
        var adapter = FragmentTablePagerAdapter(this)
        adapter.addTab(CardTextFragment::class.java, null, getString(R.string.card_text))
        adapter.addTab(CardImageFragment::class.java, null, getString(R.string.card_image))
        adapter.addTab(QrCodeFragment::class.java, null, getString(R.string.qr_code))
        card_viewpager.adapter = adapter
        card_pager_tabstrip.tabIndicatorColor = resources.getColor(R.color.orange)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            loadView()
        }
    }
}
