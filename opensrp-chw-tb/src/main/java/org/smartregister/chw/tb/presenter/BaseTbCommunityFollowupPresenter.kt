package org.smartregister.chw.tb.presenter

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.ViewModel
import com.nerdstone.neatformcore.domain.model.NFormViewData
import org.json.JSONException
import org.json.JSONObject
import org.smartregister.chw.tb.contract.BaseTbFollowupContract
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.model.BaseReferralFollowupModel
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

open class BaseTbCommunityFollowupPresenter(
    view: BaseTbFollowupContract.View,
    private val viewModelClass: Class<out BaseReferralFollowupModel?>,
    protected var interactor: BaseTbFollowupContract.Interactor
) : BaseTbFollowupContract.Presenter, BaseTbFollowupContract.InteractorCallBack {

    private val viewReference = WeakReference(view)
    var tbMemberObject: TbMemberObject? = null

    override fun saveForm(valuesHashMap: HashMap<String, NFormViewData>, jsonObject: JSONObject) =
        try {
            interactor.saveFollowup(tbMemberObject!!.baseEntityId!!, valuesHashMap, jsonObject, this)
        } catch (e: JSONException) {
            Timber.e(Log.getStackTraceString(e))
        } catch (e: SQLiteException) {
            Timber.e(Log.getStackTraceString(e))
        }

    override fun getView(): BaseTbFollowupContract.View? = viewReference.get()

    override fun <T> getViewModel(): Class<T> where T : ViewModel {
        return viewModelClass as Class<T>

    }

    override fun fillProfileData(tbMemberObject: TbMemberObject?) {
        if (tbMemberObject != null && getView() != null) {
            getView()?.setProfileViewWithData()
        }
    }

    override fun onFollowupSaved() = Unit

    override fun initializeMemberObject(tbMemberObject: TbMemberObject) {
        this.tbMemberObject = tbMemberObject
    }
}