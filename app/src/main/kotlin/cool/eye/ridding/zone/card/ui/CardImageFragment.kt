package cool.eye.ridding.zone.card.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cool.eye.ridding.R
import cool.eye.ridding.ui.BaseFragment
import cool.eye.ridding.zone.card.helper.CardHelper
import cool.eye.ridding.zone.helper.LocalStorage
import kotlinx.android.synthetic.main.card_image_item.view.*
import kotlinx.android.synthetic.main.recyclerview.view.*
import java.io.File

/**
 * Created by cool on 17-2-8.
 */
class CardImageFragment : BaseFragment() {

    lateinit var recyclerview: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.recyclerview, container, false)
        recyclerview = view.recyclerview as RecyclerView
        recyclerview.layoutManager = GridLayoutManager(activity, 2)
        recyclerview.itemAnimator = DefaultItemAnimator()
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       loadImages()
    }

    fun loadImages(){
        val dir = File(LocalStorage.composeCardImageDir().toString())
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
            holder.view.draweeview.setImageURI("file://${images[position].path}")
            holder.view.setOnClickListener { CardHelper.shareImage(context, images[position]) }
            holder.view.setOnLongClickListener {
                AlertDialog.Builder(context)
                        .setMessage("确定要删除吗?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", { dialog, witch ->
                            File(images[position].path).delete()
                            loadImages()
                        }).show()
                false
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CardHolder {
            return CardHolder(LayoutInflater.from(context).inflate(R.layout.card_image_item, parent, false))
        }
    }

    inner class CardHolder(var view: View) : RecyclerView.ViewHolder(view)

}