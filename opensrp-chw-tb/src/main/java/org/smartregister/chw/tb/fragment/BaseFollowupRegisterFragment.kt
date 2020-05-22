package org.smartregister.chw.tb.fragment

import android.content.Context
import android.database.Cursor
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.contract.BaseTbRegisterFragmentContract
import org.smartregister.chw.tb.model.BaseTbRegisterFragmentModel
import org.smartregister.chw.tb.presenter.BaseTbRegisterFragmentPresenter
import org.smartregister.chw.tb.provider.FollowupRegisterProvider
import org.smartregister.commonregistry.CommonPersonObjectClient
import org.smartregister.configurableviews.model.View
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter
import org.smartregister.cursoradapter.RecyclerViewProvider
import org.smartregister.view.customcontrols.CustomFontTextView
import org.smartregister.view.customcontrols.FontVariant
import org.smartregister.view.fragment.BaseRegisterFragment
import java.util.*

const val CLICK_VIEW_NORMAL = "click_view_normal"
const val FOLLOW_UP_VISIT = "follow_up_visit"

/**
 * Fragment used for referral Followup, extends OpenSRP's [BaseRegisterFragment] and implements [BaseTbRegisterFragmentContract.View]
 */
open class BaseFollowupRegisterFragment : BaseRegisterFragment(),
    BaseTbRegisterFragmentContract.View {

    @Suppress("INACCESSIBLE_TYPE")
    override fun initializeAdapter(visibleColumns: Set<View>?) {
        val followupRegisterProvider = FollowupRegisterProvider(
            (activity as Context), paginationViewHandler, registerActionHandler, visibleColumns!!
        )
        clientAdapter = RecyclerViewPaginatedAdapter<FollowupRegisterProvider.RegisterViewHolder>(
            null as Cursor?,
            followupRegisterProvider as RecyclerViewProvider<RecyclerView.ViewHolder>,
            context().commonrepository(tablename)
        )
        clientAdapter.setCurrentlimit(20)
        clientsView.adapter = clientAdapter
    }

    override fun setupViews(view: android.view.View) {
        super.setupViews(view)
        // Update title name
        view.findViewById<CustomFontTextView>(R.id.txt_title_label)?.apply {
            visibility = android.view.View.VISIBLE
            text = getString(R.string.followup_referral)
            setFontVariant(FontVariant.REGULAR)
        }
        view.findViewById<ImageView>(org.smartregister.R.id.opensrp_logo_image_view)?.apply {
            visibility = android.view.View.GONE
        }
        // Update Search bar
        view.findViewById<android.view.View>(org.smartregister.R.id.search_bar_layout).apply {
            setBackgroundResource(R.color.customAppThemeBlue)
        }
        getSearchView()?.apply {
            setBackgroundResource(R.color.white)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, 0, 0)
        }
        // Update top left icon
        view.findViewById<ImageView>(org.smartregister.R.id.scanQrCode)?.apply {
            visibility = android.view.View.GONE
        }
        // Update sort filter
        view.findViewById<TextView>(org.smartregister.R.id.filter_text_view)?.apply {
            text = getString(R.string.sort)
        }
    }

    override fun setUniqueID(s: String) {
        if (getSearchView() != null) {
            getSearchView().setText(s)
        }
    }

    override fun presenter() = presenter as BaseTbRegisterFragmentContract.Presenter

    override fun initializePresenter() {
        if (activity == null) {
            return
        }
        presenter =
            BaseTbRegisterFragmentPresenter(this, BaseTbRegisterFragmentModel(), null)
    }

    override fun getDefaultSortQuery() = presenter().getDefaultSortQuery()

    override fun getMainCondition() = presenter().getMainCondition()

    override fun setAdvancedSearchFormData(hashMap: HashMap<String, String>) = Unit

    override fun startRegistration() = Unit

    override fun onViewClicked(view: android.view.View) {
        if (activity == null) {
            return
        }
        if (view.tag is CommonPersonObjectClient) {
            when {
                view.getTag(R.id.VIEW_ID) === CLICK_VIEW_NORMAL -> {
                    openProfile(view.tag as CommonPersonObjectClient)
                }
                view.getTag(R.id.VIEW_ID) === FOLLOW_UP_VISIT -> {
                    openFollowUpVisit(view.tag as CommonPersonObjectClient)
                }
            }
        }
    }

    override fun showNotFoundPopup(s: String) = Unit

    protected open fun openProfile(client: CommonPersonObjectClient?) = Unit

    protected open fun openFollowUpVisit(client: CommonPersonObjectClient?) = Unit
}