package org.smartregister.chw.tb.presenter

import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.smartregister.chw.tb.TestTbApp
import org.smartregister.chw.tb.contract.BaseTbRegisterFragmentContract
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.model.BaseTbCommunityFollowupModel
import org.smartregister.chw.tb.util.Constants
import org.smartregister.commonregistry.CommonPersonObjectClient
import java.util.*

/**
 * Test class for testing various methods in BaseTbCommunityFollowupPresenter
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = TestTbApp::class)
class BaseTbCommunityFollowupPresenterTest {

    private val tbFollowupReferralView: BaseTbRegisterFragmentContract.View = spyk()
    private val tbCommunityFollowupModel = BaseTbCommunityFollowupModel()
    private val sampleBaseEntityId = "5a5mple-b35eent"
    private val tbCommunityFollowupPresenter: BaseTbRegisterFragmentContract.Presenter =
        spyk(
            BaseTbCommunityFollowupPresenter(
                tbFollowupReferralView,
                tbCommunityFollowupModel,
                ""
            ),
            recordPrivateCalls = true
        )
    private lateinit var tbMemberObject: TbMemberObject

    @Before
    fun `Before test`() {
        val columnNames = BaseTbCommunityFollowupModel()
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
    fun `Should call initialize query parameters on the view`() {
        val condition = "is_closed = 0"
        val mainTable = tbCommunityFollowupPresenter.getMainTable();
        tbCommunityFollowupPresenter.initializeQueries(condition)
        verify {
            tbFollowupReferralView.initializeQueryParams(
                mainTable,
                "SELECT COUNT(*) FROM ec_tb_community_followup WHERE is_closed = 0 ",
                "Select ec_tb_community_followup.id as _id , ec_tb_community_followup.relationalid FROM ec_tb_community_followup WHERE is_closed = 0 "
            )
            tbFollowupReferralView.initializeAdapter(TreeSet())
            tbFollowupReferralView.countExecute()
            tbFollowupReferralView.filterandSortInInitializeQueries()
        }
    }

    @Test
    fun `Should return view`() {
        Assert.assertNotNull(tbCommunityFollowupPresenter.getView())
    }

    @Test
    fun `Should return sort query`() {
        Assert.assertNotNull(tbCommunityFollowupPresenter.getDefaultSortQuery())
    }

    @Test
    fun `Should return main table`() {
        Assert.assertEquals(
            Constants.Tables.TB_COMMUNITY_FOLLOWUP,
            tbCommunityFollowupPresenter.getMainTable()
        )
    }
}