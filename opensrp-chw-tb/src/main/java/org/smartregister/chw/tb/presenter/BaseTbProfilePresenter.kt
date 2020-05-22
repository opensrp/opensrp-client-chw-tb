package org.smartregister.chw.tb.presenter

import org.smartregister.chw.tb.contract.BaseTbProfileContract
import org.smartregister.chw.tb.contract.BaseTbProfileContract.InteractorCallback
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.domain.AlertStatus
import org.smartregister.view.contract.BaseProfileContract
import java.util.*

class BaseTbProfilePresenter(
    override val view: BaseTbProfileContract.View?,
    val interactor: BaseTbProfileContract.Interactor,
    var tbMemberObject: TbMemberObject
) : BaseProfileContract, BaseTbProfileContract.Presenter,
    InteractorCallback {
    override fun refreshProfileData() {
        view?.showFollowUpVisitButton(true)
        interactor.refreshProfileView(tbMemberObject, false, this)
    }

    override fun refreshProfileTbStatusInfo() {
        interactor.updateProfileTbStatusInfo(tbMemberObject, this)
    }

    override fun refreshLastVisit(lastVisitDate: Date?) {
        view?.updateLastVisitRow(lastVisitDate)
    }

    override fun refreshProfileTopSection(tbMemberObject: TbMemberObject?) {
        view?.setProfileViewDetails(tbMemberObject)
        view?.showProgressBar(false)
    }

    override fun refreshUpComingServicesStatus(
        service: String?,
        status: AlertStatus?,
        date: Date?
    ) {
        view?.setUpComingServicesStatus(service, status, date)
    }

    override fun refreshFamilyStatus(status: AlertStatus?) {
        view?.setFamilyStatus(status)
    }
}