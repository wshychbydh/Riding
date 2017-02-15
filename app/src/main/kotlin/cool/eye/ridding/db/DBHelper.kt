package cool.eye.ridding.db

import android.content.ContentValues
import android.content.Context
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

        fun get(context: Context): DBHelper {
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
        values.put(SQLiteHelper.PHONE, blackList.phone)
        values.put(SQLiteHelper.DATA, Passenger.toJson(blackList))
        database.insert(SQLiteHelper.BLACK_LIST, null, values)
    }

    fun saveBlackList(blackList: MutableList<Passenger>) {
        clearBlackList()
        val database = sqlite_helper.writableDatabase
        database.beginTransaction()
        blackList.filterNotNull().forEach { passenger ->
            var values = ContentValues()
            values.put(SQLiteHelper.PHONE, passenger.phone)
            values.put(SQLiteHelper.DATA, Passenger.toJson(passenger))
            database.insert(SQLiteHelper.BLACK_LIST, null, values)
        }
        database.setTransactionSuccessful()
        database.endTransaction()
    }

    fun getPassengers(): List<Passenger> {
        val database = sqlite_helper.readableDatabase
        val cursor = database.query(SQLiteHelper.PASSENGER, null, null, null, null, null, null)
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

    fun getBlackList(): List<Passenger> {
        val database = sqlite_helper.readableDatabase
        val cursor = database.query(SQLiteHelper.BLACK_LIST, null, null, null, null, null, null)
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

    fun getBlackListByPhone(phone: String): Passenger? {
        val database = sqlite_helper.readableDatabase
        val cursor = database.query(SQLiteHelper.BLACK_LIST, null, "${SQLiteHelper.PHONE}=$phone", null, null, null, null)
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            return Passenger.parseJson(cursor.getString(cursor.getColumnIndex(SQLiteHelper.DATA)))
        }
        return null
    }

    fun getPassengerByPhone(phone: String): Passenger? {
        val database = sqlite_helper.readableDatabase
        val cursor = database.query(SQLiteHelper.PASSENGER, null, "${SQLiteHelper.PHONE}=$phone", null, null, null, null)
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