package org.smartregister.chw.tb.fragment


import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.model.BaseTbCommunityFollowupModel
import org.smartregister.chw.tb.presenter.BaseTbCommunityFollowupPresenter
import org.smartregister.commonregistry.CommonPersonObjectClient
import org.smartregister.view.customcontrols.CustomFontTextView
import org.smartregister.view.customcontrols.FontVariant

open class BaseTbCommunityFollowupRegisterFragment : BaseTbRegisterFragment() {

    override fun setupViews(view: android.view.View) {
        super.setupViews(view)
        view.findViewById<CustomFontTextView>(R.id.txt_title_label)?.apply {
            visibility = android.view.View.VISIBLE
            text = getString(R.string.tb_community_followup_clients)
            setFontVariant(FontVariant.REGULAR)
        }
    }

    override fun initializePresenter() {
        if (activity == null) {
            return
        }
        presenter =
            BaseTbCommunityFollowupPresenter(this, BaseTbCommunityFollowupModel(), null)
    }

    override fun openProfile(client: CommonPersonObjectClient?) = Unit
}