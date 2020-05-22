package org.smartregister.chw.tb.contract

import org.smartregister.view.contract.BaseRegisterContract

interface BaseTbRegisterContract {

    interface View : BaseRegisterContract.View {
        fun presenter(): Presenter?
    }

    interface Presenter: BaseRegisterContract.Presenter {

        fun getView(): View?
    }

    interface Model {

        fun registerViewConfigurations(viewIdentifiers: List<String?>?)

        fun unregisterViewConfiguration(viewIdentifiers: List<String?>?)

        fun saveLanguage(language: String?)

        fun getLocationId(locationName: String?): String?

        val initials: String?
    }

}