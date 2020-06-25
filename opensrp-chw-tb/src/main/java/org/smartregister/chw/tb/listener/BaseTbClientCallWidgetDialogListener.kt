package org.smartregister.chw.tb.listener

import android.view.View
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.fragment.BaseTbCallDialogFragment
import org.smartregister.chw.tb.util.TbUtil
import timber.log.Timber

/**
 * This is the listener implementation for the provided [callDialogFragment]. It handles the click listeners
 */
class BaseTbClientCallWidgetDialogListener(private val callDialogFragment: BaseTbCallDialogFragment) :
    View.OnClickListener {
    override fun onClick(view: View) {
        when (view.id) {
            R.id.tb_call_close -> {
                callDialogFragment.dismiss()
            }
            R.id.tb_call_head_phone, R.id.call_tb_client_phone -> {
                try {
                    val phoneNumber = view.tag as String
                    TbUtil.launchDialer(callDialogFragment.activity, callDialogFragment, phoneNumber)
                    callDialogFragment.dismiss()
                } catch (e: IllegalStateException) {
                    Timber.e(e)
                }
            }
        }
    }

}