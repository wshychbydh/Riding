package cool.eye.ridding.broadcast

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Toast
import cool.eye.ridding.db.DBHelper

class PhoneReceiver : BroadcastReceiver() {

    private var mContext: Context? = null

    override fun onReceive(context: Context, intent: Intent) {
        mContext = context
        //如果是去电
        if (intent.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            val phoneNumber = intent
                    .getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            val passenger = DBHelper.getBlackListByPhone(phoneNumber)
            if (passenger == null) {
                Toast.makeText(context, "打电话" + phoneNumber, Toast.LENGTH_SHORT).show()
            } else {
                kotlin.repeat(3, {
                    Toast.makeText(context, passenger.showRemark(), Toast.LENGTH_LONG).show()
                })
            }
        } else {
            /**查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电.
             * 如果我们想要监听电话的拨打状况，需要这么几步 :
             * 第一：获取电话服务管理器TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
             * 第二：通过TelephonyManager注册我们要监听的电话状态改变事件。manager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
             * 这里的PhoneStateListener.LISTEN_CALL_STATE就是我们想要监听的状态改变事件，初次之外，还有很多其他事件哦。
             * 第三步：通过extends PhoneStateListener来定制自己的规则。将其对象传递给第二步作为参数。
             * 第四步：这一步很重要，那就是给应用添加权限。android.permission.READ_PHONE_STATE */
            val tm = context.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    //设置一个监听器
    internal var listener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            //注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
            super.onCallStateChanged(state, incomingNumber)
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> println("挂断")
                TelephonyManager.CALL_STATE_OFFHOOK -> println("接听")
                TelephonyManager.CALL_STATE_RINGING -> {
                    val passenger = DBHelper.getBlackListByPhone(incomingNumber)
                    if (passenger == null) {
                        Toast.makeText(mContext, "来电监听" + incomingNumber, Toast.LENGTH_SHORT).show()
                    } else {
                        kotlin.repeat(3, {
                            Toast.makeText(mContext, passenger.showRemark(), Toast.LENGTH_LONG).show()
                        })
                    }
                }
            }
        }
    }
}   