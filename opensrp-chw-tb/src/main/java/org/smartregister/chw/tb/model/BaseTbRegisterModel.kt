package org.smartregister.chw.tb.model

import org.smartregister.chw.tb.contract.BaseTbRegisterContract

open class BaseTbRegisterModel :
    BaseTbRegisterContract.Model {

    override fun registerViewConfigurations(viewIdentifiers: List<String?>?) = Unit

    override fun unregisterViewConfiguration(viewIdentifiers: List<String?>?) = Unit

    override fun saveLanguage(language: String?) = Unit

    override fun getLocationId(locationName: String?): String? = null

    override val initials: String?
        get() = null
}