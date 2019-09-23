package cool.eye.ridding.crash

import android.content.Context
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cool.eye.ridding.util.Utils
import cool.eye.ridding.zone.helper.LocalStorage
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Created by cool on 17-2-17.
 */
object CrashHelper {

    fun uploadCrash(json: String, result: (uploadStatus: Boolean) -> Unit) {
        var crash = Crash.parseJson(json)
        crash.save(object : SaveListener<String?>() {
            override fun done(p0: String?, p1: BmobException?) {
                result.invoke(p1 == null)
            }
        })
    }

    fun uploadAllCrash(context: Context) {
        if (!Utils.isNetworkAvailable(context)) {
            return
        }
        var dir = File(LocalStorage.composeCrashFileDir().toString())
        val files = dir.listFiles()
        files?.forEach { file ->
            var json = BufferedReader(FileReader(file)).readText()
            uploadCrash(json) { result ->
                if (result) {
                    file.delete()
                }
            }
        }
    }
}