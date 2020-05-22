package org.smartregister.chw.tb.custom_views

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import org.smartregister.chw.tb.R
import timber.log.Timber

/**
 * Custom class implementation for launching clipboard as [Dialog]
 *
 * @constructor creates a dialog from the and applies the given style
 *
 * */
class ClipboardDialog(context: Context, style: Int) : Dialog(context, style), View.OnClickListener {

    @JvmField
    @VisibleForTesting
    var content: String? = null

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.widget_copy_to_clipboard)
        findViewById<View>(R.id.copyToClipboardMessage).setOnClickListener(this)
        (findViewById<View>(R.id.copyToClipboardHeader) as TextView).text = content
    }

    override fun onClick(v: View) {
        try {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip =
                ClipData.newPlainText(context.getString(R.string.copy_to_clipboard), content)
            clipboard.primaryClip = clip
            dismiss()
        } catch (e: IllegalStateException) {
            Timber.e(e)
        }
    }
}