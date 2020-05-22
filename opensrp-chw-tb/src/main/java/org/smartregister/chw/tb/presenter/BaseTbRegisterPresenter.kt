package org.smartregister.chw.tb.presenter

import org.smartregister.chw.tb.contract.BaseTbRegisterContract
import java.lang.ref.WeakReference

open class BaseTbRegisterPresenter(
    view: BaseTbRegisterContract.View,
    protected var model: BaseTbRegisterContract.Model
) : BaseTbRegisterContract.Presenter {

    private var viewReference = WeakReference(view)

    override fun getView() = viewReference.get()

    override fun registerViewConfigurations(list: List<String>?) = Unit

    override fun unregisterViewConfiguration(list: List<String>?) = Unit

    override fun onDestroy(b: Boolean) = Unit

    override fun updateInitials() = Unit

}