package cool.eye.ridding.zone.card.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eye.cool.photo.utils.ImageUtil
import cool.eye.ridding.R
import cool.eye.ridding.ui.BaseFragment
import cool.eye.ridding.util.ThreadUtil
import cool.eye.ridding.zone.card.helper.CardHelper
import cooleye.scan.decode.DecodeHelper
import cool.eye.ridding.zone.helper.LocalStorage
import kotlinx.android.synthetic.main.qrcode_item.view.*
import kotlinx.android.synthetic.main.recyclerview.view.*
import java.io.File

/**
 * Created by cool on 17-2-8.
 */
class QrCodeFragment : BaseFragment() {
    lateinit var recyclerview: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.recyclerview, container, false)
        recyclerview = view.recyclerview as RecyclerView
        recyclerview.layoutManager = GridLayoutManager(activity, 2)
        recyclerview.itemAnimator = DefaultItemAnimator()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadQrCode()
    }

    fun loadQrCode() {
        val dir = File(LocalStorage.composeQrImageDir().toString())
        if (dir.exists()) {
            val images = dir.listFiles()
            recyclerview.adapter = CardAdapter(images)
        }
    }

    inner class CardAdapter(var images: Array<File>) : RecyclerView.Adapter<CardHolder>() {
        override fun getItemCount(): Int {
            return images.size
        }

        override fun onBindViewHolder(holder: CardHolder, position: Int) {
            ThreadUtil.sync({
                ImageUtil.getBitmapFromFile("file://${images[position].path}")
            },{
                holder.view.qrcodeIv.setImageBitmap(it)
            })
            holder.view.setOnLongClickListener {
                CardHelper.shareImage(requireContext(), images[position])
                false
            }
            holder.view.setOnClickListener {
                var builder = AlertDialog.Builder(requireContext())
                        .setMessage("识别二维码?")
                        .setNegativeButton("删除") { _, _ ->
                            File(images[position].path).delete()
                            loadQrCode()
                        }
                        .setNeutralButton("取消", null)

                if (holder.view.tv_decode.text.isNullOrEmpty()) {
                    builder.setPositiveButton("解码") { _, _ ->
                        var result = DecodeHelper.decode(images[position])
                        holder.view.tv_decode.text = result
                        toast(result)
                    }
                }
                builder.show()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
            return CardHolder(LayoutInflater.from(context).inflate(R.layout.qrcode_item, parent, false))
        }
    }

    inner class CardHolder(var view: View) : RecyclerView.ViewHolder(view)

}