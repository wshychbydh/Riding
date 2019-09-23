package cool.eye.ridding.launch

import android.app.Application
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobConfig
import cool.eye.ridding.crash.CrashHandler


/**
 * Created by cool on 17-1-24.
 */
class BaseApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    CrashHandler.init()
    //自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
    val config = BmobConfig.Builder(this)
        //设置appkey
        .setApplicationId("fec8cf39da55fb598458738c34036b01")
        //请求超时时间（单位为秒）：默认15s
        .setConnectTimeout(30)
        //文件分片上传时每片的大小（单位字节），默认512*1024
        .setUploadBlockSize(1024 * 1024)
        //文件的过期时间(单位为秒)：默认1800s
        .setFileExpiration(2500)
        .build()
    Bmob.initialize(config)
    //      BmobUpdateAgent.initAppVersion() //据说只需要执行一次
    // Fresco.initialize(this)
  }
}