package cool.eye.ridding.zone.card.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import android.widget.Toast
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.support.OnSelectListener
import com.eye.cool.photo.support.v4.PhotoDialogFragment
import com.eye.cool.photo.utils.ImageUtil
import cool.eye.ridding.R
import cool.eye.ridding.util.ThreadUtil
import cool.eye.ridding.zone.card.colorpicker.picker.ColorPicker
import cool.eye.ridding.zone.card.db.DBHelper
import cool.eye.ridding.zone.card.helper.CardHelper
import kotlinx.android.synthetic.main.activity_card_add.*
import kotlinx.android.synthetic.main.common_title.*

class CardAddActivity : AppCompatActivity() {
    lateinit var bitmap: Bitmap
    var bgColor: Int = 0
    var fontColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_add)
        rightIv.setImageResource(R.drawable.share)
        bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        bgColor = resources.getColor(R.color.white)
        fontColor = resources.getColor(R.color.title)
        tv_title.text = getString(R.string.card_add)
        iv_back.setOnClickListener { finish() }
        btn_card_qr.setOnClickListener {
            if (cardInfoAvailable()) {
                DBHelper.saveCardInfo(this@CardAddActivity, et_card_content.text.toString())
                CardHelper.toQrCode(this, et_card_content.text?.trim().toString(), bitmap) { bitmap ->
                    if (bitmap != null) iv_show.setImageBitmap(bitmap)
                }
            }
        }
        btn_card_img.setOnClickListener {
            if (cardInfoAvailable()) {
                DBHelper.saveCardInfo(this@CardAddActivity, et_card_content.text.toString())
                CardHelper.toImage(this, et_card_content.text.toString().trim(),
                    et_card_content.paint, fontColor, bgColor) { bitmap ->
                    if (bitmap != null) iv_show.setImageBitmap(bitmap)
                }
            }
        }

        rightIv.setOnClickListener {
            if (cardInfoAvailable()) {
                DBHelper.saveCardInfo(this@CardAddActivity, et_card_content.text.toString())
                CardHelper.shareMsg(this, et_card_content.text.toString().trim())
            }
        }

        image_select.setOnClickListener {
            PhotoDialogFragment.Builder()
                .requestCameraPermission(true)
                .setImageParams(
                    ImageParams.Builder()
                        .setOutput(100, 100)
                        .setCutAble(true)
                        .setOnSelectListener(object : OnSelectListener {
                            override fun onSelect(path: String) {
                                ThreadUtil.sync({
                                    ImageUtil.getBitmapFromFile(path)
                                },{
                                    bitmap = it ?: bitmap
                                    image_select.setImageBitmap(bitmap)
                                })
                            }
                        })
                        .build()
                )
                .build()
                .show(supportFragmentManager)
        }

        font_size_select.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                et_card_content.textSize = progress + 5f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        font_color_select.setOnClickListener {
            var colorPicker = ColorPicker(this)
            colorPicker.setOnColorPickListener { pickedColor ->
                fontColor = pickedColor
                font_color_select.setBackgroundColor(pickedColor)
                et_card_content.setTextColor(pickedColor)
            }
            colorPicker.show()
        }

        color_select.setOnClickListener {
            var colorPicker = ColorPicker(this)
            colorPicker.setOnColorPickListener { pickedColor ->
                color_select.setBackgroundColor(pickedColor)
                bgColor = pickedColor
                et_card_content.setBackgroundColor(pickedColor)
            }
            colorPicker.show()
        }
    }

    private fun cardInfoAvailable(): Boolean {
        val text = et_card_content.text.toString()
        if (text.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.card_content_input), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}