package org.smartregister.chw.tb.presenter

import io.mockk.spyk
import io.mockk.verifySequence
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.smartregister.chw.tb.TestReferralApp
import org.smartregister.chw.tb.contract.BaseTbRegisterFragmentContract
import org.smartregister.chw.tb.model.BaseTbRegisterFragmentModel
import org.smartregister.chw.tb.util.Constants
import org.smartregister.chw.tb.util.DBConstants

@RunWith(RobolectricTestRunner::class)
@Config(application = TestReferralApp::class)
class BaseTbRegisterFragmentPresenterTest {

    private val tbRegisterFragmentView: BaseTbRegisterFragmentContract.View = spyk()

    private val tbRegisterFragmentPresenter: BaseTbRegisterFragmentContract.Presenter =
        spyk(
            BaseTbRegisterFragmentPresenter(
                tbRegisterFragmentView, BaseTbRegisterFragmentModel(), null
            ),
            recordPrivateCalls = true
        )


    @Test
    fun `Should initialize the queries on the view`() {
        tbRegisterFragmentPresenter.initializeQueries("ec_tb_register.client_tb_status_during_registration")
        val visibleColumns =
            (tbRegisterFragmentPresenter as BaseTbRegisterFragmentPresenter).visibleColumns
        verifySequence {
            tbRegisterFragmentView.initializeQueryParams(
                "ec_tb_register",
                "SELECT COUNT(*) FROM ec_tb_register WHERE ec_tb_register.client_tb_status_during_registration ",
                "Select ec_tb_register.id as _id , ec_tb_register.relationalid FROM ec_tb_register WHERE ec_tb_register.client_tb_status_during_registration "
            )
            tbRegisterFragmentView.initializeAdapter(visibleColumns)
            tbRegisterFragmentView.countExecute()
            tbRegisterFragmentView.filterandSortInInitializeQueries()
        }
    }

    @Test
    fun `Main condition should be initialize by empty string`() {
        assertTrue(tbRegisterFragmentPresenter.getMainCondition().isEmpty())
    }

    @Test
    fun `Should return the right table name`() {
        assertTrue(tbRegisterFragmentPresenter.getMainTable() == Constants.Tables.TB)
    }

    @Test
    fun `Should return the due filter query`() {
        assertEquals(
            tbRegisterFragmentPresenter.getDueFilterCondition(),
            "ec_tb_register.client_tb_status_during_registration = '${Constants.TbStatus.UNKNOWN}'"
        )
    }

    @Test
    fun `Should return default sort query`() {
        assertEquals(
            tbRegisterFragmentPresenter.getDefaultSortQuery(),
            Constants.Tables.TB + "." + DBConstants.Key.TB_REGISTRATION_DATE + " DESC "
        )
    }

    @Test
    fun `View should not be null`() {
        assertNotNull(tbRegisterFragmentPresenter.getView())
    }
}