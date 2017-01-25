//package cool.eye.ridding.db
//
//import android.content.Context
//import cool.eye.ridding.data.CarryInfo
//
///**
// * Created by cool on 17-1-17.
// */
//object CarryHelper {
//
//    private const val CARRY_SHARED = "carry_shared"
//    private const val CARRY_ID = "carry_id"
//    private const val CARRY_START_ADDRESS = "start_address"
//    private const val CARRY_END_ADDRESS = "end_address"
//    private const val CARRY_GO_OFF = "go_off"
//    private const val CARRY_PRICE = "price"
//    private const val CARRY_PEOPLE_COUNT = "people_count"
//    private const val PEOPLE_LAST_COUNT = "people_last_count"
//    private const val CARRY_MARK = "mark"
//
//    fun saveCarryInfo(context: Context, carryInfo: CarryInfo) {
//        var shared = context.getSharedPreferences(CARRY_SHARED, Context.MODE_PRIVATE)
//        val editor = shared.edit()
//        editor.clear().apply()
//        editor.putString(CARRY_START_ADDRESS, carryInfo.startAddress!!.name)
//                .putString(CARRY_ID, carryInfo.objectId)
//                .putString(CARRY_END_ADDRESS, carryInfo.endAddress!!.name)
//                .putString(CARRY_GO_OFF, carryInfo.goOffTime!!)
//                .putInt(CARRY_PRICE, carryInfo.price!!)
//                .putInt(CARRY_PEOPLE_COUNT, carryInfo.peopleCount!!)
//                .putInt(PEOPLE_LAST_COUNT, carryInfo.peopleCount!!)
//                .putString(CARRY_MARK, carryInfo.mark ?: "")
//                .apply()
//    }
//
//    fun deleteCarryInfo(context: Context) {
//        var shared = context.getSharedPreferences(CARRY_SHARED, Context.MODE_PRIVATE)
//        val editor = shared.edit()
//        editor.clear().apply()
//    }
//
////    fun updatePeopleLastCount(context: Context, count: Int) {
////        var shared = context.getSharedPreferences(CARRY_SHARED, Context.MODE_PRIVATE)
////        var lastCount = getPeopleLastCount(context) - count
////        shared.edit().putInt(PEOPLE_LAST_COUNT, lastCount)
////    }
//
//    fun getCarryId(context: Context): String? {
//        return context.getSharedPreferences(CARRY_SHARED, Context.MODE_PRIVATE).getString(CARRY_ID, null)
//    }
//
//
//    fun getStartAddress(context: Context): String? {
//        return context.getSharedPreferences(CARRY_SHARED, Context.MODE_PRIVATE).getString(CARRY_START_ADDRESS, null)
//    }
//
//    fun getEndAddress(context: Context): String? {
//        return context.getSharedPreferences(CARRY_SHARED, Context.MODE_PRIVATE).getString(CARRY_END_ADDRESS, null)
//    }
//
//    fun getGoOffTime(context: Context): String? {
//        return context.getSharedPreferences(CARRY_SHARED, Context.MODE_PRIVATE).getString(CARRY_GO_OFF, null)
//    }
//
//    fun getPrice(context: Context): Int {
//        return context.getSharedPreferences(CARRY_SHARED, Context.MODE_PRIVATE).getInt(CARRY_PRICE, 60)
//    }
//
//    fun getPeopleCount(context: Context): Int {
//        return context.getSharedPreferences(CARRY_SHARED, Context.MODE_PRIVATE).getInt(CARRY_PEOPLE_COUNT, 4)
//    }
////
////    fun getRidingPeopleCount(context: Context): Int {
////        return getPeopleCount(context) - getPeopleLastCount(context)
////    }
////
////    fun getPeopleLastCount(context: Context): Int {
////        return context.getSharedPreferences(CARRY_SHARED, Context.MODE_PRIVATE).getInt(PEOPLE_LAST_COUNT, 4)
////    }
//
////    fun getMark(context: Context): String? {
////        return context.getSharedPreferences(CARRY_SHARED, Context.MODE_PRIVATE).getString(CARRY_MARK, null)
////    }
//}