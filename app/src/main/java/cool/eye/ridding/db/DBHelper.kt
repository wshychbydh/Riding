package cool.eye.ridding.db

import android.content.ContentValues
import android.content.Context
import cool.eye.ridding.data.BlackList
import cool.eye.ridding.data.Passenger
import java.util.*

/**
 * Created by cool on 17-2-7.
 */
class DBHelper {

    companion object {
        internal const val NAME = "contacts"
        internal const val VERSION = 1
        lateinit internal var sqlite_helper: SQLiteHelper
        internal var helper: DBHelper? = null

        @JvmStatic fun get(context: Context): DBHelper {
            if (helper == null) {
                init(context)
            }
            return helper!!
        }

        @Synchronized internal fun init(context: Context) {
            if (helper == null) {
                helper = DBHelper()
                sqlite_helper = SQLiteHelper(context, NAME, null, VERSION)
            }
        }
    }

    fun savePassenger(passenger: Passenger) {
        val database = sqlite_helper.writableDatabase
        var values = ContentValues()
        values.put(SQLiteHelper.USER_ID, passenger.userId)
        values.put(SQLiteHelper.PHONE, passenger.phone)
        values.put(SQLiteHelper.DATA, Passenger.toJson(passenger))
        database.insert(SQLiteHelper.PASSENGER, null, values)
    }

    fun savePassengers(passengers: MutableList<Passenger>) {
        clearPassenger()
        val database = sqlite_helper.writableDatabase
        database.beginTransaction()
        passengers.filterNotNull().forEach { passenger ->
            var values = ContentValues()
            values.put(SQLiteHelper.USER_ID, passenger.userId)
            values.put(SQLiteHelper.PHONE, passenger.phone)
            values.put(SQLiteHelper.DATA, Passenger.toJson(passenger))
            database.insert(SQLiteHelper.PASSENGER, null, values)
        }
        database.setTransactionSuccessful()
        database.endTransaction()
    }

    fun saveBlackList(blackList: Passenger) {
        val database = sqlite_helper.writableDatabase
        var values = ContentValues()
        values.put(SQLiteHelper.USER_ID, blackList.userId)
        values.put(SQLiteHelper.PHONE, blackList.phone)
        values.put(SQLiteHelper.DATA, Passenger.toJson(blackList))
        database.insert(SQLiteHelper.BLACK_LIST, null, values)
    }

//    fun saveShareBlackList(blackList: BlackList) {
//        val database = sqlite_helper.writableDatabase
//        var values = ContentValues()
//        values.put(SQLiteHelper.PHONE, blackList.phone)
//        values.put(SQLiteHelper.DATA, BlackList.toJson(blackList))
//        database.insert(SQLiteHelper.BLACK_LIST_SHARE, null, values)
//    }

    fun saveBlackList(blackList: MutableList<Passenger>) {
        clearBlackList()
        val database = sqlite_helper.writableDatabase
        database.beginTransaction()
        blackList.filterNotNull().forEach { passenger ->
            var values = ContentValues()
            values.put(SQLiteHelper.USER_ID, passenger.userId)
            values.put(SQLiteHelper.PHONE, passenger.phone)
            values.put(SQLiteHelper.DATA, Passenger.toJson(passenger))
            database.insert(SQLiteHelper.BLACK_LIST, null, values)
        }
        database.setTransactionSuccessful()
        database.endTransaction()
    }

    fun saveShareBlackList(blackList: MutableList<BlackList>) {
        clearBlackList()
        val database = sqlite_helper.writableDatabase
        database.beginTransaction()
        blackList.filterNotNull().forEach { blackList ->
            var values = ContentValues()
            values.put(SQLiteHelper.PHONE, blackList.phone)
            values.put(SQLiteHelper.DATA, BlackList.toJson(blackList))
            database.insert(SQLiteHelper.BLACK_LIST_SHARE, null, values)
        }
        database.setTransactionSuccessful()
        database.endTransaction()
    }

    fun getPassengers(userId: String): List<Passenger> {
        val database = sqlite_helper.readableDatabase
        val cursor = database.query(SQLiteHelper.PASSENGER, null, "${SQLiteHelper.USER_ID}=?", arrayOf(userId), null, null, null)
        if (cursor != null && cursor.count > 0) {
            var passengers = ArrayList<Passenger>()
            while (cursor.moveToNext()) {
                var data = cursor.getString(cursor.getColumnIndex(SQLiteHelper.DATA))
                passengers.add(Passenger.parseJson(data))
            }
            return passengers
        }
        return listOf()
    }

    fun getBlackList(userId: String): List<Passenger> {
        val database = sqlite_helper.readableDatabase
        val cursor = database.query(SQLiteHelper.BLACK_LIST, null, "${SQLiteHelper.USER_ID}=?", arrayOf(userId), null, null, null)
        if (cursor != null && cursor.count > 0) {
            var passengers = ArrayList<Passenger>()
            while (cursor.moveToNext()) {
                var data = cursor.getString(cursor.getColumnIndex(SQLiteHelper.DATA))
                passengers.add(Passenger.parseJson(data))
            }
            return passengers
        }
        return listOf()
    }

    fun getShareBlackList(): List<BlackList> {
        val database = sqlite_helper.readableDatabase
        val cursor = database.query(SQLiteHelper.BLACK_LIST_SHARE, null, null, null, null, null, null)
        if (cursor != null && cursor.count > 0) {
            var blackList = ArrayList<BlackList>()
            while (cursor.moveToNext()) {
                var data = cursor.getString(cursor.getColumnIndex(SQLiteHelper.DATA))
                blackList.add(BlackList.parseJson(data))
            }
            return blackList
        }
        return listOf()
    }

    fun getBlackListByPhone(phone: String, userId: String?): Passenger? {
        if (userId == null) return null
        val database = sqlite_helper.readableDatabase
        val cursor = database.query(SQLiteHelper.BLACK_LIST, null, "${SQLiteHelper.USER_ID}=? and ${SQLiteHelper.PHONE}=?", arrayOf(userId, phone), null, null, null)
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            return Passenger.parseJson(cursor.getString(cursor.getColumnIndex(SQLiteHelper.DATA)))
        }
        return null
    }

    fun getShareBlackListByPhone(phone: String): BlackList? {
        val database = sqlite_helper.readableDatabase
        val cursor = database.query(SQLiteHelper.BLACK_LIST_SHARE, null, "${SQLiteHelper.PHONE}=?", arrayOf(phone), null, null, null)
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            return BlackList.parseJson(cursor.getString(cursor.getColumnIndex(SQLiteHelper.DATA)))
        }
        return null
    }

    fun getPassengerByPhone(phone: String, userId: String?): Passenger? {
        if (userId == null) return null
        val database = sqlite_helper.readableDatabase
        val cursor = database.query(SQLiteHelper.PASSENGER, null, "${SQLiteHelper.USER_ID}=? and ${SQLiteHelper.PHONE}=?", arrayOf(userId, phone), null, null, null)
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            return Passenger.parseJson(cursor.getString(cursor.getColumnIndex(SQLiteHelper.DATA)))
        }
        return null
    }

    fun clearPassenger() {
        sqlite_helper.writableDatabase.delete(SQLiteHelper.PASSENGER, null, null)
    }

    fun clearBlackList() {
        sqlite_helper.writableDatabase.delete(SQLiteHelper.BLACK_LIST, null, null)
    }
}