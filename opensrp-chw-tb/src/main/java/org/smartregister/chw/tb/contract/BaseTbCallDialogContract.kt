package org.smartregister.chw.tb.contract

import android.content.Context

interface BaseTbCallDialogContract {
    interface View {
        var pendingCallRequest: Dialer?
        val currentContext: Context?
    }

    interface Dialer {
        fun callMe()
    }
}