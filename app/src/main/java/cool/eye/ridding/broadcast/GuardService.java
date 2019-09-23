//package cool.eye.ridding.broadcast;
//
//import android.app.Service;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.IBinder;
//
//import cool.eye.ridding.zone.contacts.PassengerUtils;
//
///**
// * @author ycb
// * @date 2015-4-23
// */
//public class GuardService extends Service {
//
//    private PhoneReceiver mReceiver;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    public void onCreate() {
//        super.onCreate();
//        startForeground(0, null);
//        if (PassengerUtils.isPassengerMonitor(this)) {
//            mReceiver = new PhoneReceiver();
//            // 动态注册广播
//            /*
//            *  <action android:name="android.intent.action.SCREEN_ON"/>
//                <action android:name="android.intent.action.SCREEN_OFF"/>
//                <action android:name="android.net.conn.CONNECTIVITY_CHANGE"/>
//                <action android:name="android.net.wifi.WIFI_STATE_CHANGED"/>
//                <action android:name="android.net.wifi.STATE_CHANGE"/>
//            * */
//            IntentFilter filter = new IntentFilter();
//            filter.setPriority(1000);
//            filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
//            filter.addAction("android.intent.action.PHONE_STATE");
//            registerReceiver(mReceiver, filter);
//        }
//    }
//
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return Service.START_STICKY;
//    }
//
//    public void onDestroy() {
//        super.onDestroy();
//        stopForeground(true);
//        if (mReceiver != null)
//            unregisterReceiver(mReceiver);
//        if (PassengerUtils.isPassengerMonitor(this)) {
//            //手动关闭就不需要重启了
//            // 在此重新启动
//            startService(new Intent(this, GuardService.class));
//        }
//    }
//}
