package cool.eye.ridding.db

import android.content.Context
import java.util.*

/**
 * Created by cool on 17-2-6.
 */
object PhoneHelper {

    const val PHONE_DB = "phone_db"
    const val PHONES = "phones"

    fun savePhone(context: Context, phone: String) {
        val preferences = context.getSharedPreferences(PHONE_DB, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val set = preferences.getStringSet(PHONES, HashSet())
        set.add(phone)
        editor.putStringSet(PHONES, set).apply()
    }
}