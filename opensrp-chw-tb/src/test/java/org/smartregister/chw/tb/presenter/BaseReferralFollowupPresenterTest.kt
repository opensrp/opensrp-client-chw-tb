package org.smartregister.chw.tb.presenter

import com.nerdstone.neatformcore.domain.model.NFormViewData
import io.mockk.spyk
import io.mockk.verifyAll
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.smartregister.chw.tb.contract.BaseTbFollowupContract
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.model.BaseReferralFollowupModel
import org.smartregister.chw.tb.model.BaseRegisterFormModel
import org.smartregister.chw.tb.util.Constants
import org.smartregister.commonregistry.CommonPersonObjectClient

class BaseReferralFollowupPresenterTest {

    private val tbFollowupReferralView: BaseTbFollowupContract.View = spyk()
    private val tbFollowupReferralInteractor: BaseTbFollowupContract.Interactor = spyk()
    private val sampleBaseEntityId = "5a5mple-b35eent"
    private val tbFollowupReferralPresenter: BaseTbFollowupContract.Presenter =
        spyk(
            BaseReferralFollowupPresenter(
                tbFollowupReferralView,
                BaseReferralFollowupModel::class.java,
                tbFollowupReferralInteractor
            ),
            recordPrivateCalls = true
        )
    private lateinit var tbMemberObject: TbMemberObject

    @Before
    fun `Before test`() {
        val columnNames = BaseRegisterFormModel()
            .mainColumns(Constants.Tables.FAMILY_MEMBER).map {
                it.replace("${Constants.Tables.FAMILY_MEMBER}.", "")
            }.toTypedArray()

        val columnValues = arrayListOf(
            "10689167-07ca-45a4-91c0-1406c9ceb881", sampleBaseEntityId, "first_name", "middle_name",
            "last_name", "unique_id", "male", "1985-01-01T03:00:00.000+03:00", null
        )
        val details = columnNames.zip(columnValues).toMap()
        tbMemberObject = TbMemberObject(
            CommonPersonObjectClient(sampleBaseEntityId, details, "Some Cool Name").apply {
                columnmaps = details
            }
        )
    }

    @Test
    fun `Should call save followup method of interactor`() {
        val valuesHashMap = hashMapOf<String, NFormViewData>()
        val jsonFormObject = JSONObject()
        tbFollowupReferralPresenter.initializeMemberObject(tbMemberObject)
        tbFollowupReferralPresenter.saveForm(valuesHashMap, jsonFormObject)
        verifyAll {
            tbFollowupReferralInteractor.saveFollowup(
                sampleBaseEntityId, valuesHashMap, jsonFormObject,
                tbFollowupReferralPresenter as BaseReferralFollowupPresenter
            )
        }
    }

    @Test
    fun `Should return view`() {
        Assert.assertNotNull(tbFollowupReferralPresenter.getView())
    }

    @Test
    fun `Should call set profile view data`() {
        tbFollowupReferralPresenter.fillProfileData(spyk(tbMemberObject))
        verifyAll { tbFollowupReferralView.setProfileViewWithData() }
    }

    @Test
    fun initializeMemberObject() {
        tbFollowupReferralPresenter.initializeMemberObject(tbMemberObject)
        Assert.assertNotNull((tbFollowupReferralPresenter as BaseReferralFollowupPresenter).tbMemberObject)
    }
}