package cool.eye.ridding.zone.contacts

import android.content.Context
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cool.eye.ridding.data.BlackList
import cool.eye.ridding.data.Passenger

/**
 * Created by cool on 17-3-1.
 */
object PassengerUtils {

    const val PASSENGER_SHARED = "passenger_shared"
    const val PASSENGER_MONITOR = "passenger_monitor"
    const val BLACK_LIST_CALL = "black_list_call"

    fun setPassengerMonitor(context: Context, monitor: Boolean) {
        val preferences = context.getSharedPreferences(PASSENGER_SHARED, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(PASSENGER_MONITOR, monitor).apply()
    }

    @JvmStatic fun isPassengerMonitor(context: Context): Boolean {
        return context.getSharedPreferences(PASSENGER_SHARED, Context.MODE_PRIVATE).getBoolean(PASSENGER_MONITOR, true)
    }

    fun setBlackListCall(context: Context, call: Boolean) {
        val preferences = context.getSharedPreferences(PASSENGER_SHARED, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(BLACK_LIST_CALL, call).apply()
    }

    fun isBlackListCall(context: Context): Boolean {
        return context.getSharedPreferences(PASSENGER_SHARED, Context.MODE_PRIVATE).getBoolean(BLACK_LIST_CALL, false)
    }

    fun saveBlackList(passenger: Passenger) {
        var bmobQuery = BmobQuery<BlackList>()
        bmobQuery.addWhereEqualTo("phone", passenger.phone)
        bmobQuery.findObjects(object : FindListener<BlackList>() {
            override fun done(p0: MutableList<BlackList>?, p1: BmobException?) {
                var blackList: BlackList
                if (p1 == null) {
                    blackList = p0!![0]
                    if (blackList.signUser?.contains(passenger.objectId) ?: false) {
                        return
                    }
                    blackList.signCount += 1
                    blackList.addUnique("signUser", passenger.objectId)
                    blackList.addUnique("remark", passenger.remark)
                } else {
                    blackList = BlackList()
                    blackList.name = passenger.name
                    blackList.phone = passenger.phone
                    blackList.age = passenger.age
                    blackList.job = passenger.job
                    blackList.sex = passenger.sex
                    blackList.signCount = 1
                    blackList.signUser = mutableListOf(passenger.objectId)
                    blackList.remark = mutableListOf(passenger.remark)
                }
                blackList.save(object : SaveListener<String?>() {
                    override fun done(p0: String?, p1: BmobException?) {
                    }
                })
            }
        })
    }
}