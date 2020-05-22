package org.smartregister.chw.tb.interactor

import com.google.gson.Gson
import com.nerdstone.neatformcore.domain.model.NFormViewData
import org.json.JSONObject
import org.koin.core.inject
import org.smartregister.chw.tb.TbLibrary
import org.smartregister.chw.tb.contract.BaseRegisterFormsContract
import org.smartregister.chw.tb.util.Constants
import org.smartregister.chw.tb.util.JsonFormUtils
import org.smartregister.chw.tb.util.Util.processEvent
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
                jsonObject, Constants.EventType.REGISTRATION
            )
        Timber.i("Event = %s", Gson().toJson(event))
        processEvent(tbLibrary, event)

        callBack.onRegistrationSaved(true)
    }

}