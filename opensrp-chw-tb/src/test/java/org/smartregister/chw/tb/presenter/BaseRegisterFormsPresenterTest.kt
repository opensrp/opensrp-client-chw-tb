package org.smartregister.chw.tb.presenter

import androidx.lifecycle.ViewModel
import com.nerdstone.neatformcore.domain.model.NFormViewData
import io.mockk.spyk
import io.mockk.verifyAll
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.smartregister.chw.tb.contract.BaseRegisterFormsContract
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.model.AbstractRegisterFormModel
import org.smartregister.chw.tb.model.BaseRegisterFormModel
import org.smartregister.chw.tb.util.Constants
import org.smartregister.chw.tb.util.DBConstants
import org.smartregister.commonregistry.CommonPersonObjectClient

class BaseRegisterFormsPresenterTest {

    private val registerFormsView: BaseRegisterFormsContract.View = spyk()
    private val registerFormsInteractor: BaseRegisterFormsContract.Interactor = spyk()
    private val sampleBaseEntityId = "5a5mple-b35eent"
    private val baseRegisterFormsPresenter: BaseRegisterFormsContract.Presenter =
        spyk(
            BaseRegisterFormsPresenter(
                sampleBaseEntityId, registerFormsView, AbstractRegisterFormModel::class.java,
                registerFormsInteractor
            ),
            recordPrivateCalls = true
        )
    private lateinit var tbMemberObject: TbMemberObject

    @Before
    fun `Just before tests`() {
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
    fun `Should return view`() {
        Assert.assertNotNull(baseRegisterFormsPresenter.getView())
    }

    @Test
    fun `Should return the view model`() {
        Assert.assertNotNull(baseRegisterFormsPresenter.getViewModel<AbstractRegisterFormModel>() is ViewModel)
    }

    @Test
    fun `Should return main condition with the base entity id`() {
        Assert.assertTrue(
            baseRegisterFormsPresenter.getMainCondition() == "${Constants.Tables.FAMILY_MEMBER}.${DBConstants.Key.BASE_ENTITY_ID}  = '$sampleBaseEntityId'"
        )
    }

    @Test
    fun `Should return ec_family_member as the main table`() {
        Assert.assertTrue(baseRegisterFormsPresenter.getMainTable() == Constants.Tables.FAMILY_MEMBER)
    }

    @Test
    fun `Should set profile with data  `() {
        baseRegisterFormsPresenter.fillClientData(tbMemberObject)
        verifyAll { registerFormsView.setProfileViewWithData() }
    }

    @Test
    fun `Should update the member object`() {
        baseRegisterFormsPresenter.initializeMemberObject(tbMemberObject)
        Assert.assertNotNull((baseRegisterFormsPresenter as BaseRegisterFormsPresenter).tbMemberObject)
    }

    @Test
    fun `Should call save registration method of interactor`() {
        val valuesHashMap = hashMapOf<String, NFormViewData>()
        val jsonFormObject = JSONObject()
        baseRegisterFormsPresenter.saveForm(valuesHashMap, jsonFormObject)
        verifyAll {
            registerFormsInteractor.saveRegistration(
                sampleBaseEntityId, valuesHashMap, jsonFormObject,
                baseRegisterFormsPresenter as BaseRegisterFormsPresenter
            )
        }
    }
}