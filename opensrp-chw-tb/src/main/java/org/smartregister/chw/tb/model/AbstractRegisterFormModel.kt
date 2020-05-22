package org.smartregister.chw.tb.model

import androidx.lifecycle.ViewModel
import org.smartregister.chw.tb.contract.BaseRegisterFormsContract
import org.smartregister.chw.tb.domain.TbMemberObject

abstract class AbstractRegisterFormModel : ViewModel(),
    BaseRegisterFormsContract.Model {

    var tbMemberObject: TbMemberObject? = null
}