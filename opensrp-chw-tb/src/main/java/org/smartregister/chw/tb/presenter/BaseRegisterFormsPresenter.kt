package org.smartregister.chw.tb.presenter

import android.app.Activity
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.ViewModel
import com.nerdstone.neatformcore.domain.model.NFormViewData
import org.apache.commons.lang3.tuple.Triple
import org.json.JSONException
import org.json.JSONObject
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.contract.BaseRegisterFormsContract
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.model.AbstractRegisterFormModel
import org.smartregister.chw.tb.util.Constants
import org.smartregister.chw.tb.util.DBConstants
import org.smartregister.util.Utils
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

open class BaseRegisterFormsPresenter(
    val baseEntityID: String,
    view: BaseRegisterFormsContract.View,
    private val viewModelClass: Class<out AbstractRegisterFormModel>,
    protected var interactor: BaseRegisterFormsContract.Interactor
) : BaseRegisterFormsContract.Presenter, BaseRegisterFormsContract.InteractorCallBack {

    var tbMemberObject: TbMemberObject? = null
    private var viewReference = WeakReference(view)

    override fun getView(): BaseRegisterFormsContract.View? {
        return viewReference.get()
    }

    override fun <T> getViewModel(): Class<T> where T : ViewModel, T : BaseRegisterFormsContract.Model {
        return viewModelClass as Class<T>
    }

    override fun getMainCondition() =
        "${Constants.Tables.FAMILY_MEMBER}.${DBConstants.Key.BASE_ENTITY_ID}  = '$baseEntityID'"

    override fun getMainTable() = Constants.Tables.FAMILY_MEMBER

    override fun fillClientData(tbMemberObject: TbMemberObject?) {
        if (getView() != null) {
            getView()?.setProfileViewWithData()
        }
    }

    override fun initializeMemberObject(tbMemberObject: TbMemberObject?) {
        this.tbMemberObject = tbMemberObject
    }

    override fun saveForm(valuesHashMap: HashMap<String, NFormViewData>, jsonObject: JSONObject) {
        try {
            interactor.saveRegistration(baseEntityID, valuesHashMap, jsonObject, this)
        } catch (e: JSONException) {
            Timber.e(Log.getStackTraceString(e))
        } catch (e: SQLiteException) {
            Timber.e(Log.getStackTraceString(e))
        }
    }

    override fun onUniqueIdFetched(triple: Triple<String, String, String>, entityId: String) = Unit

    override fun onNoUniqueId() = Unit

    override fun onRegistrationSaved(saveSuccessful: Boolean,encounterType: String) {
        val context = getView() as Activity
        val toastMessage = when {
            saveSuccessful && encounterType == Constants.EventType.REGISTRATION -> context.getString(R.string.successful_registration)
            saveSuccessful && encounterType == Constants.EventType.TB_CASE_CLOSURE -> context.getString(R.string.successful_tb_case_closure)
            saveSuccessful && encounterType == Constants.EventType.FOLLOW_UP_VISIT -> context.getString(R.string.successful_visit)
            saveSuccessful && encounterType == Constants.EventType.TB_OUTCOME -> context.getString(R.string.successful_visit)
            else -> context.getString(R.string.form_not_saved)
        }
        Utils.showToast(context, toastMessage)
    }
}