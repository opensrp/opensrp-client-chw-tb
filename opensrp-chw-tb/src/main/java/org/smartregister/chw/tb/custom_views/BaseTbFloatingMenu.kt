package org.smartregister.chw.tb.custom_views

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.fragment.BaseTbClientCallDialogFragment.Companion.launchDialog
import org.smartregister.chw.tb.util.Util.getFullName

class BaseTbFloatingMenu(context: Context?, val tbMemberObject: TbMemberObject) :
    LinearLayout(context), View.OnClickListener {
    fun initUi() {
        View.inflate(context, R.layout.tb_call_floating_menu, this)
        val fab: FloatingActionButton = findViewById(R.id.tb_fab)
        fab.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.tb_fab) {
            val activity = context as Activity
            launchDialog(
                activity,
                getFullName(tbMemberObject),
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