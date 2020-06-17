package org.smartregister.chw.tb.interactor

import com.google.gson.Gson
import com.nerdstone.neatformcore.domain.model.NFormViewData
import org.json.JSONObject
import org.koin.core.inject
import org.smartregister.chw.anc.util.NCUtils
import org.smartregister.chw.tb.TbLibrary
import org.smartregister.chw.tb.contract.BaseRegisterFormsContract
import org.smartregister.chw.tb.dao.TbDao
import org.smartregister.chw.tb.util.Constants
import org.smartregister.chw.tb.util.JsonFormConstants
import org.smartregister.chw.tb.util.JsonFormUtils
import org.smartregister.chw.tb.util.TbUtil.processEvent
import timber.log.Timber
import java.util.*

/**
 * This interactor class provides actual implementations for all the functionality used in the
 * Referral forms, it implements [BaseRegisterFormsContract.Interactor]
 */
class BaseRegisterFormsInteractor : BaseRegisterFormsContract.Interactor {

    val tbLibrary by inject<TbLibrary>()


    @Throws(Exception::class)
    override fun saveRegistration(
        baseEntityId: String, valuesHashMap: HashMap<String, NFormViewData>,
        jsonObject: JSONObject, callBack: BaseRegisterFormsContract.InteractorCallBack
    ) {
        val event =
            JsonFormUtils.processJsonForm(
                tbLibrary, baseEntityId, valuesHashMap,
                jsonObject, jsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE)
            )
        JsonFormUtils.tagEvent(tbLibrary, event)
        when {
            jsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE) == Constants.EventType.TB_OUTCOME || jsonObject.getString(
                JsonFormConstants.ENCOUNTER_TYPE
            ) == Constants.EventType.TB_COMMUNITY_FOLLOWUP -> event.locationId =
                TbDao.getSyncLocationId(baseEntityId) //Necessary for syncing the event back to the chw
        }

        Timber.i("Event = %s", Gson().toJson(event))
        NCUtils.processEvent(
            event.baseEntityId,
            JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(event))
        )
        callBack.onRegistrationSaved(true, jsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE))
    }

}