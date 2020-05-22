package org.smartregister.chw.tb.contract

interface BaseTbClientCallDialogContract {

    interface View {

        var pendingCallRequest: Dialer?

    }

    interface Model {
        var name: String?
    }

    interface Dialer {
        fun callMe()
    }
}