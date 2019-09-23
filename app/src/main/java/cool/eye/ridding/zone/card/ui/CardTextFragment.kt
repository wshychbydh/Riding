package cool.eye.ridding.zone.card.ui

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cool.eye.ridding.R
import cool.eye.ridding.ui.BaseFragment
import cool.eye.ridding.ui.DividerDecoration
import cool.eye.ridding.zone.card.db.DBHelper
import cool.eye.ridding.zone.card.helper.CardHelper
import kotlinx.android.synthetic.main.card_text_item.view.*
import kotlinx.android.synthetic.main.recyclerview.view.*


/**
 * Created by cool on 17-2-8.
 */
class CardTextFragment : BaseFragment() {

    lateinit var recyclerview: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.recyclerview, container, false)
        recyclerview = view.recyclerview as RecyclerView
        recyclerview.layoutManager = LinearLayoutManager(context)
        recyclerview.itemAnimator = DefaultItemAnimator()
        recyclerview.addItemDecoration(DividerDecoration(context))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadText()
    }

    fun loadText() {
        recyclerview.adapter = CardAdapter(DBHelper.getCardInfo(requireContext()))
    }

    inner class CardAdapter(var infos: List<String>) : RecyclerView.Adapter<CardHolder>() {
        override fun getItemCount(): Int {
            return infos.size
        }

        override fun onBindViewHolder(holder: CardHolder, position: Int) {
            holder.view.tv_card_content.text = infos[position]
            holder.view.setOnClickListener {
                AlertDialog.Builder(requireContext())
                        .setMessage("你要做什么?")
                        .setNegativeButton("删除") { _, _ ->
                            DBHelper.removeCardInfo(requireContext(), infos[position])
                            loadText()
                        }
                    .setNeutralButton("复制文本") { _, _ ->
                            copy(infos[position])
                        }
                    .setPositiveButton("确定") { _, _ ->
                            CardHelper.shareMsg(requireContext(), infos[position])
                        }.show()
                false
            }
        }

        fun copy(content: String) {
            // 从API11开始android推荐使用android.content.ClipboardManager
            // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
            val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = content
            toast("复制成功")
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
            return CardHolder(LayoutInflater.from(context).inflate(R.layout.card_text_item, parent, false))
        }
    }

    inner class CardHolder(var view: View) : RecyclerView.ViewHolder(view)

}