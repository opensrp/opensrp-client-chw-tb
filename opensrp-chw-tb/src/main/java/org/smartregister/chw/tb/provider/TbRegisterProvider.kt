package org.smartregister.chw.tb.provider

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.DateTime
import org.joda.time.Period
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.fragment.BaseTbRegisterFragment
import org.smartregister.chw.tb.util.DBConstants
import org.smartregister.commonregistry.CommonPersonObjectClient
import org.smartregister.cursoradapter.RecyclerViewProvider
import org.smartregister.util.Utils
import org.smartregister.view.contract.SmartRegisterClient
import org.smartregister.view.dialog.FilterOption
import org.smartregister.view.dialog.ServiceModeOption
import org.smartregister.view.dialog.SortOption
import timber.log.Timber
import java.text.MessageFormat
import java.util.*
import org.smartregister.configurableviews.model.View as ConfigurableView

open class TbRegisterProvider(
    private val context: Context, private val paginationClickListener: View.OnClickListener,
    private var onClickListener: View.OnClickListener,
    private val visibleColumns: Set<ConfigurableView>?
) : RecyclerViewProvider<TbRegisterProvider.RegisterViewHolder> {

    private val inflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(
        cursor: Cursor, smartRegisterClient: SmartRegisterClient,
        registerViewHolder: RegisterViewHolder
    ) {
        if (visibleColumns!!.isEmpty())
            populatePatientColumn(
                smartRegisterClient as CommonPersonObjectClient, registerViewHolder
            )
    }

    override fun getFooterView(
        viewHolder: RecyclerView.ViewHolder, currentPageCount: Int, totalPageCount: Int,
        hasNext: Boolean, hasPrevious: Boolean
    ) {
        (viewHolder as FooterViewHolder)
            .apply {
                pageInfoView.text = MessageFormat.format(
                    context.getString(org.smartregister.R.string.str_page_info),
                    currentPageCount,
                    totalPageCount
                )
                nextPageView.visibility = if (hasNext) View.VISIBLE else View.INVISIBLE
                nextPageView.setOnClickListener(paginationClickListener)
                previousPageView.visibility = if (hasPrevious) View.VISIBLE else View.INVISIBLE
                previousPageView.setOnClickListener(paginationClickListener)
            }
    }

    override fun updateClients(
        villageFilter: FilterOption?, serviceModeOption: ServiceModeOption?,
        searchFilter: FilterOption?, sortOption: SortOption?
    ) = null

    override fun onServiceModeSelected(serviceModeOption: ServiceModeOption) = Unit

    override fun newFormLauncher(formName: String?, entityId: String?, metaData: String?) = null

    override fun inflater() = inflater

    override fun createViewHolder(parent: ViewGroup) = RegisterViewHolder(
        inflater.inflate(R.layout.tb_register_list_row_item, parent, false)
    )

    override fun createFooterHolder(parent: ViewGroup) = FooterViewHolder(
        inflater.inflate(R.layout.smart_register_pagination, parent, false)
    )

    override fun isFooterViewHolder(viewHolder: RecyclerView.ViewHolder) =
        viewHolder is FooterViewHolder

    private fun populatePatientColumn(
        pc: CommonPersonObjectClient, viewHolder: RegisterViewHolder
    ) {
        try {
            val firstName = Utils.getName(
                Utils.getValue(pc.columnmaps, DBConstants.Key.FIRST_NAME, true),
                Utils.getValue(pc.columnmaps, DBConstants.Key.MIDDLE_NAME, true)
            )
            val dobString = Utils.getValue(pc.columnmaps, DBConstants.Key.DOB, false)
            val age = Period(DateTime(dobString), DateTime()).years
            val patientName = Utils.getName(
                firstName, Utils.getValue(pc.columnmaps, DBConstants.Key.LAST_NAME, true)
            )
            with(viewHolder) {
                this.patientName.text = String.format(
                    Locale.getDefault(), "%s, %d", patientName, age
                )
                textViewGender.text = Utils.getValue(pc.columnmaps, DBConstants.Key.GENDER, true)
                textViewVillage.text =
                    Utils.getValue(pc.columnmaps, DBConstants.Key.VILLAGE_TOWN, true)

                patientColumn.apply {
                    setOnClickListener(onClickListener)
                    tag = pc
                    setTag(R.id.VIEW_ID, BaseTbRegisterFragment.CLICK_VIEW_NORMAL)
                }
                textLastVisit.apply {
                    setOnClickListener(onClickListener)
                    tag = pc
//                    TODO fix this area
//                    setTag(R.id.VIEW_ID, BaseTbRegisterFragment.FOLLOW_UP_VISIT)
                }
                registerColumns.setOnClickListener(onClickListener)
                registerColumns.setOnClickListener { patientColumn.performClick() }
            }
        } catch (e: IllegalStateException) {
            Timber.e(e)
        }
    }

    open inner class RegisterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var patientName: TextView = itemView.findViewById(R.id.patient_name_age)
        var textViewVillage: TextView = itemView.findViewById(R.id.text_view_village)
        var textViewGender: TextView = itemView.findViewById(R.id.text_view_gender)
        var textLastVisit: TextView = itemView.findViewById(R.id.text_view_last_visit)
        var patientColumn: View = itemView.findViewById(R.id.patient_column)
        var registerColumns: View = itemView.findViewById(R.id.register_columns)
        var dueWrapper: View = itemView.findViewById(R.id.due_button_wrapper)
    }

    open inner class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var pageInfoView: TextView = view.findViewById(org.smartregister.R.id.txt_page_info)
        var nextPageView: Button = view.findViewById(org.smartregister.R.id.btn_next_page)
        var previousPageView: Button = view.findViewById(org.smartregister.R.id.btn_previous_page)

    }

    companion object {
        protected var client: CommonPersonObjectClient? = null
    }
}