package cool.eye.ridding.util

import rx.Single
import rx.SingleSubscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 *Created by ycb on 2019/9/23 0023
 */
object ThreadUtil {

  fun <T> sync(async: () -> T?, sync: (T?) -> Unit) {
    Single.fromCallable {
      async.invoke()
    }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe (object : SingleSubscriber<T?>() {
          override fun onSuccess(p0: T?) {
            sync.invoke(p0)
          }

          override fun onError(p0: Throwable?) {
            p0?.printStackTrace()
          }
        })
  }
}