package cool.eye.ridding.broadcast

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Toast
import cn.bmob.v3.BmobUser
import cool.eye.ridding.db.DBHelper


class PhoneReceiver : BroadcastReceiver() {

    private lateinit var mContext: Context

    override fun onReceive(context: Context, intent: Intent) {
        mContext = context
        println("通话：${intent.action}")
        //去电
        if (intent.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            showToast(getToastMsg(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)))
        } else {
            //android.intent.action.PHONE_STATE
            //通话状态改变时触发，这里包括接听电话，挂断电话等等
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

    /**
     * CALL_STATE_IDLE 无任何状态时
     * CALL_STATE_OFFHOOK 接起电话时
     * CALL_STATE_RINGING 电话进来时
     */

    //设置一个监听器
    internal var listener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            //注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
            super.onCallStateChanged(state, incomingNumber)
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> println("挂断：$state") //FIXME 以后来完成
                TelephonyManager.CALL_STATE_OFFHOOK -> println("接听：$state")
                TelephonyManager.CALL_STATE_RINGING -> {
                    showToast(incomingNumber)
                }
            }
        }
    }

    fun getToastMsg(phoneNumber: String): String? {
        var userId = BmobUser.getCurrentUser()?.objectId
        var passenger = DBHelper.get(mContext).getBlackListByPhone(phoneNumber, userId)
        if (passenger == null) {
            var blackList = DBHelper.get(mContext).getShareBlackListByPhone(phoneNumber)
            if (blackList == null) {
                return DBHelper.get(mContext).getPassengerByPhone(phoneNumber, userId)?.passenger()
            } else {
                return blackList.blackList()
            }
        } else {
            return passenger.blackList()
        }
    }

//    fun ringOff(){
//        val method = Class.forName(
//                "android.os.ServiceManager")
//                .getMethod("getService", String::class.java)
//        // 获取远程TELEPHONY_SERVICE的IBinder对象的代理
//        val binder = method.invoke(null,
//                arrayOf<Any>(TELEPHONY_SERVICE)) as IBinder
//        // 将IBinder对象的代理转换为ITelephony对象
//        val telephony = ITelephony.Stub
//                .asInterface(binder)
//        // 挂断电话
//        telephony.endCall()
//    }

    fun showToast(msg: String?) {
        if (msg != null) {
            kotlin.repeat(3, {
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
            })
        }
    }
}   