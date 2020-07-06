package org.smartregister.chw.tb.presenter

import io.mockk.spyk
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.smartregister.chw.tb.TestTbApp
import org.smartregister.chw.tb.contract.BaseTbRegisterContract
import org.smartregister.chw.tb.model.BaseTbRegisterModel

/**
 * Test class for testing various methods in BaseTbRegisterPresenter
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = TestTbApp::class)
class BaseTbRegisterPresenterTest {

    private val tbHistoryView: BaseTbRegisterContract.View = spyk()
    private val tbHistoryPresenter: BaseTbRegisterContract.Presenter =
        spyk(
            BaseTbRegisterPresenter(tbHistoryView, BaseTbRegisterModel()), recordPrivateCalls = true
        )

    @Test
    fun `Referral register view should not be null`() {
        assertNotNull(tbHistoryPresenter.getView())
    }
}