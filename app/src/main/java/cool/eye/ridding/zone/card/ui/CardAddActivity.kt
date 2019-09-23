package cool.eye.ridding.zone.card.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import cool.eye.ridding.R
import cool.eye.ridding.zone.card.colorpicker.picker.ColorPicker
import cool.eye.ridding.zone.card.db.DBHelper
import cool.eye.ridding.zone.card.helper.CardHelper
import cool.eye.ridding.zone.photo.PhotoActivity
import cool.eye.ridding.zone.photo.PhotoDialog
import kotlinx.android.synthetic.main.activity_card_add.*
import kotlinx.android.synthetic.main.common_title.*

class CardAddActivity : PhotoActivity() {
    lateinit var bitmap: Bitmap
    var bgColor: Int = 0
    var fontColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_add)
        iv_submit.setImageResource(R.drawable.share)
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

        iv_submit.setOnClickListener {
            if (cardInfoAvailable()) {
                DBHelper.saveCardInfo(this@CardAddActivity, et_card_content.text.toString())
                CardHelper.shareMsg(this, et_card_content.text.toString().trim())
            }
        }

        image_select.setOnClickListener {
            var dialog = PhotoDialog(this)
            setITakePhoto(dialog.view.photoInterface)
            dialog.view.setPhotoCallback { bitmap ->
                this@CardAddActivity.bitmap = bitmap
                image_select.setImageBitmap(bitmap)
            }
            dialog.show()
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

    fun cardInfoAvailable(): Boolean {
        val text = et_card_content.text.toString()
        if (text.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.card_content_input), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}