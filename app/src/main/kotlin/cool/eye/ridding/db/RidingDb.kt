package cooleye.eot.kotlin.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by cool on 16-11-25.
 */
class RidingDb(context: Context) : SQLiteOpenHelper(context, "Riding", null, 1) {
    companion object {
        const val TABLE_NAME = "riding"
        const val PHONE = "phone"
        const val TIME = "time"
        const val DATA = "data"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        createTables(db!!)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        createTables(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        var sb = StringBuilder()
        sb.append("CREATE TABLE IF NOT EXISTS $TABLE_NAME( ")
                .append("$PHONE INTEGER NOT NULL,")
                .append("$TIME LONG NOT NULL,")
                .append("$DATA data)")
        db!!.execSQL(sb.toString())
    }
}