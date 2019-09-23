package cool.eye.ridding.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cool.eye.ridding.zone.contacts.PassengerUtils

/**
 * Created by cool on 17-3-1.
 */
/**
 * 在Android中，有一些action是不支持静态注册的：

android.intent.action.SCREEN_ON

android.intent.action.SCREEN_OFF

android.intent.action.BATTERY_CHANGED

android.intent.action.CONFIGURATION_CHANGED

android.intent.action.TIME_TICK
 */
//class BootReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context?, intent: Intent?) {
//        println("接收到广播事件======>>" + intent?.action)
//        if (context != null &&  PassengerUtils.isPassengerMonitor(context))
//            ServiceUtil.startService(context, GuardService::class.java)
//    }
//}