package cool.eye.ridding.zone.about

import android.os.Bundle
import cool.eye.ridding.R
import cool.eye.ridding.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.common_title.*
import java.io.BufferedReader
import java.io.InputStreamReader

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        iv_back.setOnClickListener { finish() }
        tv_title.text = getString(R.string.account_about)
        tv_about_content.text = loadText()
    }

    fun loadText(): String? {
        val inputStream = assets.open("about/about")
        if (inputStream != null) {
            var br = BufferedReader(InputStreamReader(inputStream))
            var line = br.readLine()
            var sb = StringBuilder()
            while (!line.isNullOrEmpty()) {
                sb.append(line).append("\n")
                line = br.readLine()
            }
            return sb.toString()
        }
        return null
    }
}
