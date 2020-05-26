package org.smartregister.chw.tb.custom_views

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.fragment.BaseTbCallDialogFragment
import org.smartregister.chw.tb.util.TbUtil

open class BaseTbFloatingMenu (context: Context?, val tbMemberObject: TbMemberObject): LinearLayout(context), View.OnClickListener {

    open fun initUi() {
        View.inflate(context, R.layout.tb_call_floating_menu, this)
        findViewById<View>(R.id.tb_fab).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.tb_fab) {
            val activity = context as Activity
            BaseTbCallDialogFragment.launchDialog(
                activity,
                TbUtil.getFullName(this.tbMemberObject),
                tbMemberObject.phoneNumber,
                tbMemberObject.primaryCareGiver,
                tbMemberObject.primaryCareGiverPhoneNumber
            )
        }
    }

    init {
        initUi()
    }

}