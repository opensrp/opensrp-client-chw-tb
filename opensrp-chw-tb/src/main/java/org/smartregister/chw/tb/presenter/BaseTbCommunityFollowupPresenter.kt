package org.smartregister.chw.tb.presenter

import org.apache.commons.lang3.StringUtils
import org.smartregister.chw.tb.contract.BaseTbRegisterFragmentContract
import org.smartregister.chw.tb.util.Constants
import org.smartregister.chw.tb.util.DBConstants

open class BaseTbCommunityFollowupPresenter(
    view: BaseTbRegisterFragmentContract.View,
    model: BaseTbRegisterFragmentContract.Model,
    viewConfigurationIdentifier: String?
) : BaseTbRegisterFragmentPresenter(
    view, model, viewConfigurationIdentifier
) {
    override fun getDefaultSortQuery() =
        Constants.Tables.TB_COMMUNITY_FOLLOWUP + "." + DBConstants.Key.LAST_INTERACTED_WITH + " DESC "

    override fun initializeQueries(mainCondition: String) {
        val tableName = Constants.Tables.TB_COMMUNITY_FOLLOWUP
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

    override fun getMainTable() = Constants.Tables.TB_COMMUNITY_FOLLOWUP

    override fun getDueFilterCondition() =
        "${Constants.Tables.TB_COMMUNITY_FOLLOWUP}.${DBConstants.Key.IS_CLOSED} = 0"

}