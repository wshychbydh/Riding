package cool.eye.ridding.data

import cn.bmob.v3.BmobObject
import cn.bmob.v3.BmobUser

/**
 * Created by cool on 17-1-24.
 */
open class BaseBmobObject : BmobObject() {
    var userId = BmobUser.getCurrentUser()?.objectId
}