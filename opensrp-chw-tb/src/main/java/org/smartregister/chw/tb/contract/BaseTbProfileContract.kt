package org.smartregister.chw.tb.contract

import android.content.Context
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.domain.AlertStatus
import org.smartregister.view.contract.BaseProfileContract
import java.util.*

interface BaseTbProfileContract {
    interface View : BaseProfileContract.View {
        val context: Context?
        fun openMedicalHistory()
        fun openUpcomingServices()
        fun openFamilyDueServices()
        fun openTbRegistrationForm()
        fun openFollowUpVisitForm(isEdit: Boolean)
        fun setUpComingServicesStatus(
            service: String?,
            status: AlertStatus?,
            date: Date?
        )

        fun setFamilyStatus(status: AlertStatus?)
        fun setProfileViewDetails(tbMemberObject: TbMemberObject?)
        fun setupFollowupVisitEditViews(isWithin24Hours: Boolean)
        fun updateLastVisitRow(lastVisitDate: Date?)
        fun setFollowUpButtonOverdue()
        fun setFollowUpButtonDue()
        fun hideFollowUpVisitButton()
        fun showFollowUpVisitButton(status: Boolean)
        fun showProgressBar(status: Boolean)
        fun onMemberDetailsReloaded(tbMemberObject: TbMemberObject?)
    }

    interface Presenter {
        val view: View?
        fun refreshProfileData()
        fun refreshProfileTbStatusInfo()
    }

    interface Interactor {
        fun refreshProfileView(
            tbMemberObject: TbMemberObject?,
            isForEdit: Boolean,
            callback: InteractorCallback?
        )

        fun updateProfileTbStatusInfo(
            memberObject: TbMemberObject?,
            callback: InteractorCallback?
        )
    }

    interface InteractorCallback {
        fun refreshProfileTopSection(tbMemberObject: TbMemberObject?)
        fun refreshUpComingServicesStatus(
            service: String?,
            status: AlertStatus?,
            date: Date?
        )

        fun refreshFamilyStatus(status: AlertStatus?)
        fun refreshLastVisit(lastVisitDate: Date?)
    }
}