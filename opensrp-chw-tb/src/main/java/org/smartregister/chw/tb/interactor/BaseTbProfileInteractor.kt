package org.smartregister.chw.tb.interactor

import androidx.annotation.VisibleForTesting
import org.smartregister.chw.anc.util.AppExecutors
import org.smartregister.chw.tb.contract.BaseTbProfileContract
import org.smartregister.chw.tb.contract.BaseTbProfileContract.InteractorCallback
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.domain.AlertStatus
import java.util.*

class BaseTbProfileInteractor @VisibleForTesting internal constructor(
    var appExecutors: AppExecutors
) : BaseTbProfileContract.Interactor {

    constructor() : this(AppExecutors()) {}

    override fun refreshProfileView(
        tbMemberObject: TbMemberObject?,
        isForEdit: Boolean,
        callback: InteractorCallback?
    ) {
        val runnable = Runnable {
            appExecutors.mainThread()
                .execute { callback!!.refreshProfileTopSection(tbMemberObject) }
        }
        appExecutors.diskIO().execute(runnable)
    }

    override fun updateProfileTbStatusInfo(
        memberObject: TbMemberObject?,
        callback: InteractorCallback?
    ) {
        val runnable = Runnable {
            appExecutors.mainThread().execute {
                callback!!.refreshFamilyStatus(AlertStatus.normal)
                callback.refreshUpComingServicesStatus(
                    "TB Followup Visit",
                    AlertStatus.normal,
                    Date()
                )
                callback.refreshLastVisit(Date())
            }
        }
        appExecutors.diskIO().execute(runnable)
    }

}