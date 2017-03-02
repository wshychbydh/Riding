package cool.eye.ridding.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by cool on 17-2-7.
 */
class SQLiteHelper(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        const val PASSENGER = "passenger"
        const val BLACK_LIST_SHARE = "blacklist_share"
        const val BLACK_LIST = "blacklist"
        const val PHONE = "phone"
        const val USER_ID = "userId" //用户ID
        const val DATA = "data"
    }

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        removeAllTables(db)
        createTables(db)
    }

    private fun removeAllTables(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $PASSENGER")
        db.execSQL("DROP TABLE IF EXISTS $BLACK_LIST")
        db.execSQL("DROP TABLE IF EXISTS $BLACK_LIST_SHARE")
    }

    fun createTables(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS $PASSENGER ( $PHONE VARCHAR(11) PRIMARY KEY NOT NULL , $USER_ID VARCHAR(100) NOT NULL, $DATA TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS $BLACK_LIST ( $PHONE VARCHAR(11) PRIMARY KEY NOT NULL , $USER_ID VARCHAR(100) NOT NULL, $DATA TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS $BLACK_LIST_SHARE ( $PHONE VARCHAR(11) PRIMARY KEY NOT NULL , $DATA TEXT NOT NULL)")
    }
}