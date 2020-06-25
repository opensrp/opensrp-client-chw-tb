package org.smartregister.chw.tb.presenter

import org.apache.commons.lang3.StringUtils
import org.smartregister.chw.tb.contract.BaseTbRegisterFragmentContract
import org.smartregister.chw.tb.util.Constants
import org.smartregister.chw.tb.util.DBConstants
import org.smartregister.configurableviews.model.Field
import org.smartregister.configurableviews.model.RegisterConfiguration
import org.smartregister.configurableviews.model.View
import java.lang.ref.WeakReference
import java.util.*

open class BaseTbRegisterFragmentPresenter(
    view: BaseTbRegisterFragmentContract.View,
    protected var model: BaseTbRegisterFragmentContract.Model,
    protected var viewConfigurationIdentifier: String?
) : BaseTbRegisterFragmentContract.Presenter {

    protected var viewReference = WeakReference(view)
    protected var config: RegisterConfiguration
    var visibleColumns: Set<View> = TreeSet()

    override fun updateSortAndFilter(filterList: List<Field>, sortField: Field) = Unit

    override fun getMainCondition() = ""

    override fun getDefaultSortQuery() =
        Constants.Tables.TB + "." + DBConstants.Key.TB_REGISTRATION_DATE + " DESC "

    override fun processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return
        }
        val viewConfiguration =
            model.getViewConfiguration(viewConfigurationIdentifier)
        if (viewConfiguration != null) {
            config = viewConfiguration.metadata as RegisterConfiguration
            visibleColumns = model.getRegisterActiveColumns(viewConfigurationIdentifier)!!
        }
        if (config.searchBarText != null && getView() != null) {
            getView()?.updateSearchBarHint(config.searchBarText)
        }
    }

    override fun initializeQueries(mainCondition: String) {
        val tableName = Constants.Tables.TB
        val condition =
            if (StringUtils.trim(getMainCondition()) == "") mainCondition else getMainCondition()
        val countSelect = model.countSelect(tableName, condition)
        val mainSelect = model.mainSelect(tableName, condition)
        getView()?.also {
            it.initializeQueryParams(tableName, countSelect, mainSelect)
            it.initializeAdapter(visibleColumns)
            it.countExecute()
            it.filterandSortInInitializeQueries()
        }
    }

    override fun getView(): BaseTbRegisterFragmentContract.View? {
        return viewReference.get()
    }

    override fun startSync() = Unit

    override fun searchGlobally(s: String) = Unit

    override fun getMainTable() = Constants.Tables.TB

    override fun getDueFilterCondition() =
        "ec_tb_register.client_tb_status_during_registration = '${Constants.TbStatus.UNKNOWN}'"

    init {
        config = model.defaultRegisterConfiguration()!!
    }
}