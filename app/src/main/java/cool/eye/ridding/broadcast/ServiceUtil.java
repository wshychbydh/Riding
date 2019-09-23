package cool.eye.ridding.broadcast;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.Map;

/**
 * Created by cool on 16-11-2.
 */

public class ServiceUtil {
    /**
     * 定时任务
     */
    public static void startService(Context context, long millSecond, Class<? extends Service>
            clazz) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(context, 501, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, 0, millSecond, pendingIntent);
    }

    public static void startService(Context context, Class<? extends Service> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.startService(intent);
    }

    public static void startService(Context context, Class<? extends Service> clazz, Map<String,
            String> parms) {
        Intent intent = new Intent(context, clazz);
        if (parms != null && !parms.isEmpty()) {
            for (String key : parms.keySet()) {
                intent.putExtra(key, parms.get(key).toString());
            }
        }
        context.startService(intent);
    }

    /**
     * 停止定时任务
     *
     * @param context
     */
    public static void stopService(Context context, Class<? extends Service> clzz) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, clzz);
        PendingIntent pendingIntent = PendingIntent.getService(context, 501, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
        context.stopService(intent);
    }

    public static void startDeviceAdmin(Activity activity) {
        // 发送广播
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        ComponentName componentName = new ComponentName(activity.getPackageName(), activity
                .getPackageName()
                + ".MyDeviceAdminReceiver");
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        activity.startActivityForResult(intent, 0);
    }
}
