package org.smartregister.chw.tb.model

import androidx.lifecycle.ViewModel
import org.smartregister.chw.tb.domain.TbMemberObject


open class BaseReferralFollowupModel : ViewModel() {
    var tbMemberObject: TbMemberObject? = null
}