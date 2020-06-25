package org.smartregister.chw.tb.fragment


import org.smartregister.chw.tb.model.BaseTbCommunityFollowupModel
import org.smartregister.chw.tb.presenter.BaseTbCommunityFollowupPresenter
import org.smartregister.commonregistry.CommonPersonObjectClient

open class BaseTbCommunityFollowupRegisterFragment : BaseTbRegisterFragment() {
    override fun initializePresenter() {
        if (activity == null) {
            return
        }
        presenter =
            BaseTbCommunityFollowupPresenter(this, BaseTbCommunityFollowupModel(), null)
    }

    override fun openProfile(client: CommonPersonObjectClient?) = Unit
}