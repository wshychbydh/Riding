package cooleye.eot.kotlin.db

import android.content.ContentValues
import cool.eye.ridding.BaseApplication
import cool.eye.ridding.util.Utils
import cooleye.eot.kotlin.data.Riding
import java.io.*
import java.util.*


/**
 * Created by cool on 16-11-25.
 */
object DbHelper {

    private var ridingDb: RidingDb = BaseApplication.ridingDb

    fun saveRidingInfo(data: Riding): Boolean {
        val db = ridingDb.writableDatabase
        var value = ContentValues()
        value.put(RidingDb.TIME, data.ridingTime)
        value.put(RidingDb.PHONE, data.passenger.phone)
        value.put(RidingDb.DATA, toByteArray(data))
        return db.insert(RidingDb.TABLE_NAME, null, value) > 0
    }

    fun deleteRidingInfo(phone: String) {
        val db = ridingDb.writableDatabase
        var value = ContentValues()
        value.put(RidingDb.PHONE, phone)
        db.delete(RidingDb.TABLE_NAME, "${RidingDb.PHONE} = $phone", null)
    }

    fun getRidings(): ArrayList<Riding>? {
        val db = ridingDb.readableDatabase
        var cursor = db.query(RidingDb.TABLE_NAME, null, "${RidingDb.TIME} >=? and ${RidingDb.TIME} <=?",
                arrayOf(Utils.getStartTime().toString(), Utils.getEndTime().toString()), null, null, null)
        if (cursor != null) {
            val datas = ArrayList<Riding>()
            while (cursor.moveToNext()) {
                var data = cursor.getBlob(cursor.getColumnIndex(RidingDb.DATA))
                if (data != null) {
                    var obj = toObject(data)
                    if (obj != null) {
                        datas.add(obj as Riding)
                    }
                }
            }
            return datas!!
        }
        return null
    }

    fun getRidingCount(): Int {
        val db = ridingDb.readableDatabase
        var cursor = db.query(RidingDb.TABLE_NAME, null, "${RidingDb.TIME} >=? and ${RidingDb.TIME} <=?",
                arrayOf(Utils.getStartTime().toString(), Utils.getEndTime().toString()), null, null, null)
        var count = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                var data = cursor.getBlob(cursor.getColumnIndex(RidingDb.DATA))
                if (data != null) {
                    var obj = toObject(data)
                    if (obj != null) {
                        count += (obj as Riding).peopleCount
                    }
                }
            }
            return count
        }
        return count
    }

    /**
     * 对象转数组
     * @param obj
     * *
     * @return
     */
    fun toByteArray(obj: Any): ByteArray? {
        var bytes: ByteArray? = null
        val bos = ByteArrayOutputStream()
        try {
            val oos = ObjectOutputStream(bos)
            oos.writeObject(obj)
            oos.flush()
            bytes = bos.toByteArray()
            oos.close()
            bos.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return bytes
    }

    /**
     * 数组转对象
     * @param bytes
     * *
     * @return
     */
    fun toObject(bytes: ByteArray): Any? {
        var obj: Any? = null
        try {
            val bis = ByteArrayInputStream(bytes)
            val ois = ObjectInputStream(bis)
            obj = ois.readObject()
            ois.close()
            bis.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        } catch (ex: ClassNotFoundException) {
            ex.printStackTrace()
        }

        return obj
    }
}