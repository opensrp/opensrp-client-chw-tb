package org.smartregister.chw.tb.interactor

import com.google.gson.Gson
import com.nerdstone.neatformcore.domain.model.NFormViewData
import org.json.JSONObject
import org.koin.core.inject
import org.smartregister.chw.anc.AncLibrary
import org.smartregister.chw.anc.util.NCUtils
import org.smartregister.chw.tb.TbLibrary
import org.smartregister.chw.tb.contract.BaseRegisterFormsContract
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
        Timber.i("Event = %s", Gson().toJson(event))
        processEvent(tbLibrary, event)

        when (Constants.EventType.FOLLOW_UP_VISIT) {
            jsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE) -> {
                val visit = NCUtils.eventToVisit(
                    event,
                    org.smartregister.chw.anc.util.JsonFormUtils.generateRandomUUIDString()
                )
                visit.preProcessedJson = Gson().toJson(event)
                AncLibrary.getInstance().visitRepository().addVisit(visit)
            }
        }

        callBack.onRegistrationSaved(true)
    }

}