package org.smartregister.chw.tb.presenter

import io.mockk.spyk
import org.junit.Test

import org.junit.Assert.*
import org.smartregister.chw.tb.contract.BaseTbRegisterContract
import org.smartregister.chw.tb.model.BaseTbRegisterModel

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