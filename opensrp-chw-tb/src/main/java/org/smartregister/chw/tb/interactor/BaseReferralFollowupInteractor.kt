package org.smartregister.chw.tb.interactor

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.nerdstone.neatformcore.domain.model.NFormViewData
import org.json.JSONObject
import org.koin.core.inject
import org.smartregister.chw.tb.TbLibrary
import org.smartregister.chw.tb.contract.BaseTbFollowupContract
import org.smartregister.chw.tb.util.Constants
import org.smartregister.chw.tb.util.JsonFormUtils
import timber.log.Timber
import java.util.*


/**
 * This interactor class provides actual implementations for all the functionality used in the
 * follow up referral forms, it implements [BaseTbFollowupContract.Interactor]
 */
class BaseReferralFollowupInteractor : BaseTbFollowupContract.Interactor {

    val referralLibrary by inject<TbLibrary>()

    @Throws(Exception::class)
    override fun saveFollowup(
        baseEntityId: String, valuesHashMap: HashMap<String, NFormViewData>,
        jsonObject: JSONObject, callBack: BaseTbFollowupContract.InteractorCallBack
    ) = saveFollowup(baseEntityId, valuesHashMap, jsonObject)

    @VisibleForTesting
    fun saveFollowup(
        baseEntityId: String?, valuesHashMap: HashMap<String, NFormViewData>?,
        jsonObject: JSONObject?
    ) {
        val baseEvent =
            JsonFormUtils.processJsonForm(
                referralLibrary, baseEntityId, valuesHashMap!!, jsonObject,
                Constants.EventType.REGISTRATION
            )
        baseEvent.eventId = UUID.randomUUID().toString()
        Timber.i("Followup Event = %s", Gson().toJson(baseEvent))
        //        Util.processEvent(allSharedPreferences, baseEvent);
    }
}