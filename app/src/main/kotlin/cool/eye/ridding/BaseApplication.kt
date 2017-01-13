package cool.eye.ridding

import android.app.Application

import cooleye.eot.kotlin.db.RidingDb

/**
 * Created by cool on 17-1-13.
 */

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ridingDb = RidingDb(this)
    }

    companion object {
        lateinit var ridingDb: RidingDb
    }
}
