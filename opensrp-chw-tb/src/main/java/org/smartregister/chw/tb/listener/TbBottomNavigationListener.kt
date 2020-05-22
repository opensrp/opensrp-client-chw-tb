package org.smartregister.chw.tb.listener

import android.app.Activity
import android.view.MenuItem
import org.smartregister.chw.tb.R
import org.smartregister.listener.BottomNavigationListener
import org.smartregister.view.activity.BaseRegisterActivity


/**
 * This is the listener implementation for the provided [context]. It handles the menu item selection events
 */
class TbBottomNavigationListener(private val context: Activity) :
    BottomNavigationListener(context) {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        super.onNavigationItemSelected(item)
        val baseRegisterActivity = context as BaseRegisterActivity
        when (item.itemId) {
            R.id.action_tb_clients -> {
                baseRegisterActivity.switchToBaseFragment()
            }
            R.id.action_received_referrals -> {
                baseRegisterActivity.switchToFragment(1)
            }
            R.id.action_scan_qr -> {
                baseRegisterActivity.startQrCodeScanner()
            }
        }
        return true
    }

}