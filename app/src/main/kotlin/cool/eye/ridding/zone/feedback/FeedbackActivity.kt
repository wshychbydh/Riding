package cn.sudiyi.app.client.account.page

import android.os.Bundle
import android.view.View
import cn.bmob.v3.BmobUser
import cn.sudiyi.app.client.account.support.FeedbackPickerDialog
import cn.sudiyi.app.client.account.support.FeedbackType
import cool.eye.ridding.R
import cool.eye.ridding.login.model.UserModel
import cool.eye.ridding.ui.BaseActivity
import cool.eye.ridding.util.SaveDataListener
import cool.eye.ridding.util.Utils
import cool.eye.ridding.zone.feedback.Feedback
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.common_title.*

/**
 * Created by cool on 16-9-22.
 */
class FeedbackActivity : BaseActivity() {

    private var feedbackType = FeedbackType.FUNC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        iv_back.setOnClickListener { finish() }
        tv_title.text = getString(R.string.account_feedback)
        feedback_type.text = FeedbackType.FUNC.value
        updateFeedbackCount(0)
        tv_submit.visibility = View.VISIBLE
        tv_submit.setOnClickListener { submit() }
        feedback_type.setOnClickListener { selectFeedbackType() }
        var user = BmobUser.getCurrentUser(UserModel::class.java)
        if (user.mobilePhoneNumber.isNullOrEmpty()) {
            if (!user.email.isNullOrEmpty()) {
                feedback_contacts.setText(user.email)
            }
        } else {
            feedback_contacts.setText(user.mobilePhoneNumber)
        }
    }

    private fun submit() {

        var content = feedback_content.text.trim().toString()
        if (content.isNullOrEmpty() || content.length < 6) {
            toast(getString(R.string.account_feedback_6))
            return
        }

        var contacts = feedback_contacts.text.trim().toString()
        if (contacts.isNullOrEmpty() || (!Utils.isPhoneNumberValid(contacts) && !Utils.isEmail(contacts))) {
            toast(getString(R.string.account_feedback_contacts_error))
            return
        }

        var feedback = Feedback()
        feedback.type = feedbackType.type
        feedback.contacts = contacts
        feedback.content = content
        feedback.saveData(object : SaveDataListener {
            override fun onSucceed(objectId: String) {
                toast(getString(R.string.account_feedback_succeed))
                finish()
            }
        })
    }

    private fun selectFeedbackType() {
        var dialog = FeedbackPickerDialog(this)
        dialog.onSelectionListener = { type ->
            feedbackType = type
            feedback_type.text = type.value
        }
        dialog.show()
    }

    private fun updateFeedbackCount(count: Int) {
        feedback_content_count.text = "$count / $MAX_COUNT"
    }

    companion object {
        private val MAX_COUNT = 200
    }
}
