package org.smartregister.chw.tb.activity

import android.app.Activity
import android.content.Intent
import org.smartregister.chw.anc.activity.BaseAncHomeVisitActivity
import org.smartregister.chw.anc.domain.MemberObject
import org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS
import org.smartregister.chw.tb.R
import java.text.MessageFormat

open class BaseTbFollowUpVisitActivity : BaseAncHomeVisitActivity() {
    override fun redrawHeader(memberObject: MemberObject) {
        tvTitle.text = MessageFormat.format(
            "{0}, {1} \u00B7 {2}",
            memberObject.fullName,
            memberObject.age,
            getString(R.string.tb_follow_up_visit)
        )
    }


    companion object {
        fun startMe(
            activity: Activity,
            memberObject: MemberObject?,
            isEditMode: Boolean?
        ) {
            val intent = Intent(activity, BaseTbFollowUpVisitActivity::class.java)
            intent.putExtra(ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject)
            intent.putExtra(ANC_MEMBER_OBJECTS.EDIT_MODE, isEditMode)
            activity.startActivity(intent)
        }
    }
}