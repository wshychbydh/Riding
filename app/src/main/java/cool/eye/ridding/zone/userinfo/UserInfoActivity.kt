package cool.eye.ridding.zone.userinfo

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.RadioButton
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import cool.eye.ridding.login.support.InputChecker
import cool.eye.ridding.R
import cool.eye.ridding.data.SEX
import cool.eye.ridding.login.model.UserModel
import cool.eye.ridding.zone.photo.IUploadCallback
import cool.eye.ridding.zone.photo.PhotoActivity
import cool.eye.ridding.zone.photo.PhotoDialog
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.common_title.*


class UserInfoActivity : PhotoActivity() {

    lateinit var userModel: UserModel
    var head: String? = null
    var needUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        iv_back.setOnClickListener {
            getModifyUser()
            if (needUpdate) {
                AlertDialog.Builder(this@UserInfoActivity)
                        .setMessage("你有修改的信息，退出后不会保存，确定要退出吗?")
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setPositiveButton(getString(R.string.sure), { dialogInterface, i -> finish() })
                        .show()
            } else {
                finish()
            }
        }
        tv_title.text = getString(R.string.account_userinfo_edit)
        tv_submit.visibility = View.VISIBLE
        tv_submit.text = getString(R.string.save)
        tv_submit.setOnClickListener { save() }
        userModel = BmobUser.getCurrentUser(UserModel::class.java)
        userinfo_head.setOnClickListener {
            setHead()
        }
        fillUserInfo()
    }

    fun fillUserInfo() {
        if (userModel.head != null)
            userinfo_head.setImageURI(userModel.head)
        userinfo_nickname.setText(userModel.nickname)
        userinfo_age.setText(userModel.age.toString())
        userinfo_email.setText(userModel.email)
        userinfo_phone.setText(userModel.mobilePhoneNumber)
        if (userModel.sex != null)
            (userinfo_sex.getChildAt(userModel.sex!!) as RadioButton).isChecked = true
    }

    fun save() {
        if (inputCheck()) {
            var user = getModifyUser()
            user.head = head
            if (needUpdate) {
                user.update(userModel.objectId, object : UpdateListener() {
                    override fun done(p0: BmobException?) = if (p0 == null) {
                        toast(getString(R.string.userinfo_update_succeed))
                        setResult(1001)
                        finish()
                    } else {
                        toast(p0.message ?: getString(R.string.unknow_error))
                    }
                })
            } else {
                finish()
            }
        }
    }

    fun getModifyUser(): UserModel {
        var user = UserModel()
        val nickname = userinfo_nickname.text.toString().trim()
        if (nickname != null && nickname != userModel.nickname) {
            user.nickname = nickname
            needUpdate = true
        }
        val age = userinfo_age.text.toString().toIntOrNull()
        if (age != null && age != userModel.age) {
            user.age = age
            needUpdate = true
        }
        var sex = SEX.valueOfCode(userinfo_sex.indexOfChild(userinfo_sex.findViewById(userinfo_sex.checkedRadioButtonId))).code
        if (sex != userModel.sex) {
            user.sex = sex
            needUpdate = true
        }
        val phone = userinfo_phone.text.toString().trim()
        if (phone != userModel.mobilePhoneNumber) {
            user.mobilePhoneNumber = phone
            needUpdate = true
        }
        val email = userinfo_email.text.toString().trim()
        if (email != userModel.email) {
            user.email = email
            needUpdate = true
        }
        return user
    }

    private fun inputCheck(): Boolean {
        return InputChecker.checkPhone(userinfo_phone) && InputChecker.checkEmail(userinfo_email)
    }

    private fun setHead() {
        var dialog = PhotoDialog(this)
        setITakePhoto(dialog.view.photoInterface)
        dialog.view.setUploadCallback(object : IUploadCallback {
            override fun onSucceed(fileUrl: String) {
                needUpdate = true
                head = fileUrl
                userinfo_head.setImageURI(fileUrl)
            }
        })
        dialog.show()
    }
}
