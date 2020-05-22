package org.smartregister.chw.tb.listener

import android.view.View
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.fragment.BaseTbClientCallDialogFragment
import org.smartregister.chw.tb.util.Util
import timber.log.Timber

/**
 * This is the listener implementation for the provided [callDialogFragment]. It handles the click listeners
 */
class BaseTbClientCallWidgetDialogListener(private val callDialogFragment: BaseTbClientCallDialogFragment) :
    View.OnClickListener {
    override fun onClick(view: View) {
        when (view.id) {
            R.id.tb_call_close -> {
                callDialogFragment.dismiss()
            }
            R.id.tb_call_head_phone, R.id.call_tb_client_phone -> {
                try {
                    val phoneNumber = view.tag as String
                    Util.launchDialer(callDialogFragment.activity, callDialogFragment, phoneNumber)
                    callDialogFragment.dismiss()
                } catch (e: IllegalStateException) {
                    Timber.e(e)
                }
            }
        }
    }

}