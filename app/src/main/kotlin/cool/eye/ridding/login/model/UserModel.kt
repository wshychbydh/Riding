package cool.eye.ridding.login.model

import cn.bmob.v3.BmobUser

/**
 * Created by cool on 17-1-24.
 */

class UserModel : BmobUser() {
    var age: Int? = null
    var head: String? = null
    var sex: Int? = null
}