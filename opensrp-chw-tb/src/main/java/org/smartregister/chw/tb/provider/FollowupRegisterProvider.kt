package org.smartregister.chw.tb.provider

import android.annotation.SuppressLint
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
import org.smartregister.view.contract.SmartRegisterClients
import org.smartregister.view.dialog.FilterOption
import org.smartregister.view.dialog.ServiceModeOption
import org.smartregister.view.dialog.SortOption
import org.smartregister.view.viewholder.OnClickFormLauncher
import java.text.MessageFormat
import org.smartregister.configurableviews.model.View as ConfigurableView

open class FollowupRegisterProvider(
    private val context: Context, private val paginationClickListener: View.OnClickListener,
    protected var onClickListener: View.OnClickListener,
    private val visibleColumns: Set<ConfigurableView>
) : RecyclerViewProvider<FollowupRegisterProvider.RegisterViewHolder> {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @SuppressLint("SetTextI18n")
    @Throws(Exception::class)
    private fun populatePatientColumn(
        pc: CommonPersonObjectClient, viewHolder: RegisterViewHolder
    ) {
        with(viewHolder) {
            val firstName = Utils.getName(
                Utils.getValue(pc.columnmaps, DBConstants.Key.FIRST_NAME, true),
                Utils.getValue(pc.columnmaps, DBConstants.Key.MIDDLE_NAME, true)
            )
            val patientName = Utils.getName(
                firstName, Utils.getValue(pc.columnmaps, DBConstants.Key.LAST_NAME, true)
            )
            val dobString = Utils.getValue(pc.columnmaps, DBConstants.Key.DOB, false)
            val age = Period(DateTime(dobString), DateTime()).years
            this.patientName.text = "$patientName, $age"
            textViewVillage.text = Utils.getValue(pc.columnmaps, DBConstants.Key.VILLAGE_TOWN, true)
            textViewGender.text = Utils.getValue(pc.columnmaps, DBConstants.Key.GENDER, true)

            patientColumn.apply {
                setOnClickListener(onClickListener)
                tag = pc
                setTag(R.id.VIEW_ID, BaseTbRegisterFragment.CLICK_VIEW_NORMAL)
            }

            dueButton.apply {
                setOnClickListener(onClickListener)
                tag = pc
                setTag(R.id.VIEW_ID, BaseTbRegisterFragment.FOLLOW_UP_VISIT)
            }

            registerColumns.apply {
                setOnClickListener(onClickListener)
                setOnClickListener { dueButton.performClick() }
                setOnClickListener { viewHolder.patientColumn.performClick() }
            }
        }
    }

    override fun getView(
        cursor: Cursor, smartRegisterClient: SmartRegisterClient,
        registerViewHolder: RegisterViewHolder
    ) {
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(
                smartRegisterClient as CommonPersonObjectClient, registerViewHolder
            )
        }
    }

    override fun getFooterView(
        viewHolder: RecyclerView.ViewHolder, currentPageCount: Int, totalPageCount: Int,
        hasNext: Boolean, hasPrevious: Boolean
    ) {
        val holder = viewHolder as FooterViewHolder
        holder.also {
            it.nextPageView?.visibility = if (hasNext) View.VISIBLE else View.INVISIBLE
            it.pageInfoView?.text = MessageFormat.format(
                context.getString(org.smartregister.R.string.str_page_info),
                currentPageCount, totalPageCount
            )
            it.previousPageView?.visibility = if (hasPrevious) View.VISIBLE else View.INVISIBLE
            it.previousPageView?.setOnClickListener(paginationClickListener)
            it.nextPageView?.setOnClickListener(paginationClickListener)
        }
    }

    override fun updateClients(
        filterOption: FilterOption, serviceModeOption: ServiceModeOption,
        filterOption1: FilterOption, sortOption: SortOption
    ): SmartRegisterClients? = null

    override fun onServiceModeSelected(serviceModeOption: ServiceModeOption) = Unit

    override fun newFormLauncher(s: String, s1: String, s2: String): OnClickFormLauncher? = null

    override fun inflater(): LayoutInflater = inflater

    override fun createViewHolder(parent: ViewGroup) =
        RegisterViewHolder(
            inflater.inflate(R.layout.followup_register_list_row_item, parent, false)
        )

    override fun createFooterHolder(parent: ViewGroup) =
        FooterViewHolder(inflater.inflate(R.layout.smart_register_pagination, parent, false))

    override fun isFooterViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder is FooterViewHolder
    }

    open inner class RegisterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var patientName: TextView = itemView.findViewById(R.id.patient_name_age)
        var textViewVillage: TextView = itemView.findViewById(R.id.text_view_village)
        var dueButton: Button = itemView.findViewById(R.id.due_button)
        var patientColumn: View = itemView.findViewById(R.id.patient_column)
        var dueWrapper: View = itemView.findViewById(R.id.due_button_wrapper)
        var registerColumns: View = itemView.findViewById(R.id.register_columns)
        var textViewGender: TextView = itemView.findViewById(R.id.text_view_gender)
    }

    inner class FooterViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        var previousPageView: Button? = view.findViewById(org.smartregister.R.id.btn_previous_page)
        var pageInfoView: TextView? = view.findViewById(org.smartregister.R.id.txt_page_info)
        var nextPageView: Button? = view.findViewById(org.smartregister.R.id.btn_next_page)
    }

    companion object {
        protected var client: CommonPersonObjectClient? = null
    }

}