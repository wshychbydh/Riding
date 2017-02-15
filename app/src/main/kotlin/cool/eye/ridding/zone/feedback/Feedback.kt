package cool.eye.ridding.zone.feedback

import cool.eye.ridding.data.BaseBmobObject

/**
 * Created by cool on 17-2-13.
 */
class Feedback : BaseBmobObject() {
    var contacts: String? = null
    var type: Int? = null
    var content: String? = null
    override fun toString(): String {
        return "Feedback(contacts=$contacts, type=$type, content=$content)"
    }
}