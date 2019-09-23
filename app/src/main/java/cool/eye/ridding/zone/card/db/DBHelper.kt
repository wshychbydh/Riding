package cool.eye.ridding.zone.card.db

import android.content.Context

/**
 * Created by cool on 17-2-9.
 */
object DBHelper {

    private const val CARD = "card"
    private const val CARD_INFO = "card_info"

    fun saveCardInfo(context: Context, info: String) {
        val sharedPreferences = context.getSharedPreferences(CARD, Context.MODE_PRIVATE)
        val linkedSet = sharedPreferences.getStringSet(CARD_INFO, linkedSetOf())
        linkedSet.add(info)
        sharedPreferences.edit().putStringSet(CARD_INFO, linkedSet).apply()
    }

    fun getCardInfo(context: Context): List<String> {
        val sharedPreferences = context.getSharedPreferences(CARD, Context.MODE_PRIVATE)
        val set = sharedPreferences.getStringSet(CARD_INFO, linkedSetOf())
        var list = mutableListOf<String>()
        list.addAll(set)
        return list
    }

    fun removeCardInfo(context: Context, info: String) {
        val sharedPreferences = context.getSharedPreferences(CARD, Context.MODE_PRIVATE)
        val linkedSet = sharedPreferences.getStringSet(CARD_INFO, linkedSetOf())
        linkedSet.remove(info)
        sharedPreferences.edit().putStringSet(CARD_INFO, linkedSet).apply()
    }
}