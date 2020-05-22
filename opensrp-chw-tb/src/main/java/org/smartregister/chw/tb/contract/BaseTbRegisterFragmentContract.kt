package org.smartregister.chw.tb.contract

import org.json.JSONArray
import org.koin.core.KoinComponent
import org.smartregister.configurableviews.model.Field
import org.smartregister.configurableviews.model.RegisterConfiguration
import org.smartregister.configurableviews.model.ViewConfiguration
import org.smartregister.domain.Response
import org.smartregister.view.contract.BaseRegisterFragmentContract

interface BaseTbRegisterFragmentContract {

    interface View : BaseRegisterFragmentContract.View {

        fun initializeAdapter(visibleColumns: Set<org.smartregister.configurableviews.model.View>?)

        fun presenter(): Presenter?
    }

    interface Presenter : BaseRegisterFragmentContract.Presenter {

        fun getView() : View?

        fun updateSortAndFilter(filterList: List<Field>, sortField: Field)

        fun getMainCondition(): String

        fun getDefaultSortQuery(): String

        fun getMainTable(): String

        fun getDueFilterCondition(): String
    }

    interface Model: KoinComponent {

        fun defaultRegisterConfiguration(): RegisterConfiguration?

        fun getViewConfiguration(viewConfigurationIdentifier: String?): ViewConfiguration?

        fun getRegisterActiveColumns(viewConfigurationIdentifier: String?): Set<org.smartregister.configurableviews.model.View>?

        fun countSelect(tableName: String?, mainCondition: String?): String?

        fun mainSelect(tableName: String?, mainCondition: String?): String?

        fun getFilterText(filterList: List<Field?>?, filter: String?): String?

        fun getSortText(sortField: Field?): String?

        fun getJsonArray(response: Response<String?>?): JSONArray?
    }
}