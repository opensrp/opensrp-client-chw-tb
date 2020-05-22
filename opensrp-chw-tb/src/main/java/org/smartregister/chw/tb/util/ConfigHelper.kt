package org.smartregister.chw.tb.util

import android.content.Context
import org.smartregister.R
import org.smartregister.configurableviews.model.Field
import org.smartregister.configurableviews.model.RegisterConfiguration
import java.util.*

/**
 * Helper functions for Registration config
 */
object ConfigHelper {

    /**
     * Returns [RegisterConfiguration] used on the referral register.
     */
    @JvmStatic
    fun defaultRegisterConfiguration(context: Context?): RegisterConfiguration? {
        return RegisterConfiguration().apply {
            isEnableAdvancedSearch = false
            isEnableFilterList = false
            isEnableSortList = false
            searchBarText = context?.getString(R.string.search_hint)
            isEnableJsonViews = false
            filterFields = ArrayList<Field>()
            sortFields = ArrayList<Field>()
        }
    }
}