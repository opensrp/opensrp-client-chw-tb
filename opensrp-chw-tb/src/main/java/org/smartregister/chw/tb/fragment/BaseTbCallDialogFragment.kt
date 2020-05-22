package org.smartregister.chw.tb.fragment

import android.app.Activity
import android.app.DialogFragment
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import org.apache.commons.lang3.StringUtils
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.contract.BaseTbClientCallDialogContract
import org.smartregister.chw.tb.listener.BaseTbClientCallWidgetDialogListener
import org.smartregister.util.Utils


/**
 * Fragment used for launching a call dialog; implements [BaseTbClientCallDialogContract.View]
 */
open class BaseTbCallDialogFragment : DialogFragment(),
    BaseTbClientCallDialogContract.View {

    private var listener: View.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle
    ): View {
        val dialogView = inflater.inflate(
            R.layout.tb_member_call_widget_dialog_fragment, container, false
        ) as ViewGroup
        setUpPosition()
        if (listener == null) {
            listener = BaseTbClientCallWidgetDialogListener(this)
        }
        initUI(dialogView)
        return dialogView
    }

    private fun initUI(rootView: ViewGroup) {
        if (StringUtils.isNotBlank(tbClientPhoneNumber)) {
            rootView.findViewById<TextView>(R.id.call_referral_client_name)
                ?.apply { text = tbClientName }

            rootView.findViewById<TextView>(R.id.call_tb_client_phone)?.apply {
                tag = tbClientPhoneNumber
                text = Utils.getName(getString(R.string.call), tbClientPhoneNumber)
                setOnClickListener(listener)
            }

        } else {
            rootView.findViewById<View>(R.id.layout_tb_client).visibility = View.GONE
        }
        if (StringUtils.isNotBlank(tbFamilyHeadPhone)) {

            rootView.findViewById<TextView>(R.id.tb_call_head_name)
                ?.apply { text = tbFamilyHeadName }

            rootView.findViewById<TextView>(R.id.tb_call_head_phone)
                ?.apply {
                    tag = tbFamilyHeadPhone
                    text = Utils.getName(getString(R.string.call), tbFamilyHeadPhone)
                    setOnClickListener(listener)
                }
        } else {
            rootView.findViewById<View>(R.id.tb_layout_family_head).visibility = View.GONE
        }
        rootView.findViewById<View>(R.id.tb_call_close).setOnClickListener(listener)
    }

    private fun setUpPosition() {
        dialog.window?.also {
            it.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP)
            val p = it.attributes
            with(p) {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                y = 20
            }
            it.attributes = p
        }
    }

    override var pendingCallRequest: BaseTbClientCallDialogContract.Dialer? = null

    companion object {

        const val DIALOG_TAG = "BaseTbClientCallDialogFragment_DIALOG_TAG"
        private var tbClientName: String? = null
        private var tbClientPhoneNumber: String? = null
        private var tbFamilyHeadName: String? = null
        private var tbFamilyHeadPhone: String? = null

        fun launchDialog(
            activity: Activity, clientName: String?, referralClientPhone: String?,
            familyHeadName: String?, familyHeadPhone: String?
        ): BaseTbCallDialogFragment {
            val dialogFragment = newInstance()
            val ft = activity.fragmentManager.beginTransaction()
            val prev = activity.fragmentManager.findFragmentByTag(DIALOG_TAG)
            tbClientPhoneNumber = referralClientPhone
            tbClientName = clientName
            tbFamilyHeadName = familyHeadName
            tbFamilyHeadPhone = familyHeadPhone
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, DIALOG_TAG)
            return dialogFragment
        }

        fun newInstance(): BaseTbCallDialogFragment = BaseTbCallDialogFragment()
    }
}