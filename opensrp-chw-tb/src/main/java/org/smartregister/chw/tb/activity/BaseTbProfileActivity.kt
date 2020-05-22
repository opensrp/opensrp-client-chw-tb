package org.smartregister.chw.tb.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import de.hdodenhof.circleimageview.CircleImageView
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Period
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.contract.BaseTbProfileContract
import org.smartregister.chw.tb.custom_views.BaseTbFloatingMenu
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.interactor.BaseTbProfileInteractor
import org.smartregister.chw.tb.presenter.BaseTbProfilePresenter
import org.smartregister.chw.tb.util.Constants
import org.smartregister.chw.tb.util.TbUtil.fromHtml
import org.smartregister.chw.tb.util.TbUtil.getMemberProfileImageResourceIDentifier
import org.smartregister.domain.AlertStatus
import org.smartregister.helper.ImageRenderHelper
import org.smartregister.view.activity.BaseProfileActivity
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

open class BaseTbProfileActivity : BaseProfileActivity(),
    BaseTbProfileContract.View {
    private var lastVisitRow: View? = null
    private var recordFollowUpVisitLayout: LinearLayout? = null
    private var recordVisitStatusBarLayout: RelativeLayout? = null
    private var tickImage: ImageView? = null
    private var tvEditVisit: TextView? = null
    private var tvUndo: TextView? = null
    private var tvVisitDone: TextView? = null
    private var rlLastVisitLayout: RelativeLayout? = null
    private var rlUpcomingServices: RelativeLayout? = null
    private var rlFamilyServicesDue: RelativeLayout? = null
    private var tvLastVisitDay: TextView? = null
    private var tvViewMedicalHistory: TextView? = null
    private var tvUpComingServices: TextView? = null
    private var tvFamilyStatus: TextView? = null
    private var tvRecordTbFollowUp: TextView? = null
    private var tvTbRow: TextView? = null
    var tbProfilePresenter: BaseTbProfileContract.Presenter? = null
    private var tbFloatingMenu: BaseTbFloatingMenu? = null
    var tbMemberObject: TbMemberObject? = null
    private var numOfDays = 0
    private var progressBar: ProgressBar? = null
    private var profileImageView: CircleImageView? = null
    private var tvName: TextView? = null
    private var tvGender: TextView? = null
    private var tvLocation: TextView? = null
    private var tvUniqueID: TextView? = null
    private var overDueRow: View? = null
    private var familyRow: View? = null
    override fun onCreation() {
        setContentView(R.layout.activity_base_tb_profile)
        val toolbar =
            findViewById<Toolbar>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            val upArrow =
                resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)
            upArrow.setColorFilter(
                resources.getColor(R.color.text_blue),
                PorterDuff.Mode.SRC_ATOP
            )
            actionBar.setHomeAsUpIndicator(upArrow)
        }
        toolbar.setNavigationOnClickListener { v: View? -> finish() }
        appBarLayout = findViewById(R.id.collapsing_toolbar_appbarlayout)
        if (Build.VERSION.SDK_INT >= 21) {
            appBarLayout.outlineProvider = null
        }
        tbMemberObject =
            intent.getSerializableExtra(Constants.ActivityPayload.MEMBER_OBJECT) as TbMemberObject
        setupViews()
        initializePresenter()
        fetchProfileData()
        initializeCallFAB()
    }

    override fun setupViews() {
        imageRenderHelper = ImageRenderHelper(this)
        tvName = findViewById(R.id.textview_name)
        tvGender = findViewById(R.id.textview_gender)
        tvLocation = findViewById(R.id.textview_address)
        tvUniqueID = findViewById(R.id.textview_unique_id)
        recordVisitStatusBarLayout =
            findViewById(R.id.record_visit_status_bar_layout)
        recordFollowUpVisitLayout = findViewById(R.id.record_recurring_layout)
        lastVisitRow = findViewById(R.id.view_last_visit_row)
        overDueRow = findViewById(R.id.view_most_due_overdue_row)
        familyRow = findViewById(R.id.view_family_row)
        tvUpComingServices = findViewById(R.id.textview_name_due)
        tvFamilyStatus = findViewById(R.id.textview_family_has)
        tvTbRow = findViewById(R.id.textview_tb_registration_date_row)
        rlLastVisitLayout = findViewById(R.id.rl_last_visit_layout)
        tvLastVisitDay = findViewById(R.id.textview_last_vist_day)
        tvViewMedicalHistory = findViewById(R.id.textview_medical_history)
        rlUpcomingServices = findViewById(R.id.rlUpcomingServices)
        rlFamilyServicesDue = findViewById(R.id.rlFamilyServicesDue)
        progressBar = findViewById(R.id.progress_bar)
        tickImage = findViewById(R.id.tick_image)
        tvVisitDone = findViewById(R.id.textview_visit_done)
        tvEditVisit = findViewById(R.id.textview_edit)
        tvUndo = findViewById(R.id.textview_undo)
        profileImageView =
            findViewById(R.id.imageview_profile)
        tvRecordTbFollowUp = findViewById(R.id.textview_record_reccuring_visit)
        tvUndo?.let { tvUndo?.setOnClickListener(this) }
        tvEditVisit?.let { tvEditVisit?.setOnClickListener(this) }
        tvRecordTbFollowUp?.let { tvRecordTbFollowUp?.setOnClickListener(this) }
        findViewById<View>(R.id.rl_last_visit_layout).setOnClickListener(this)
        findViewById<View>(R.id.rlUpcomingServices).setOnClickListener(this)
        findViewById<View>(R.id.rlFamilyServicesDue).setOnClickListener(this)
        findViewById<View>(R.id.rlTbRegistrationDate).setOnClickListener(this)
    }

    override fun initializePresenter() {
        tbProfilePresenter =
            BaseTbProfilePresenter(this, BaseTbProfileInteractor(), tbMemberObject!!)
    }

    fun initializeCallFAB() {
        if (StringUtils.isNotBlank(tbMemberObject!!.phoneNumber)
            || StringUtils.isNotBlank(tbMemberObject!!.familyHeadPhoneNumber)
        ) {
            tbFloatingMenu = BaseTbFloatingMenu(this, tbMemberObject!!)
            tbFloatingMenu!!.gravity = Gravity.BOTTOM or Gravity.END
            val linearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            addContentView(tbFloatingMenu, linearLayoutParams)
        }
    }

    override fun setupViewPager(viewPager: ViewPager): ViewPager? {
        return null
    }

    override fun fetchProfileData() {
        tbProfilePresenter!!.refreshProfileData()
        tbProfilePresenter!!.refreshProfileTbStatusInfo()
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.title_layout) {
            onBackPressed()
        } else if (id == R.id.rl_last_visit_layout) {
            openMedicalHistory()
        } else if (id == R.id.rlUpcomingServices) {
            openUpcomingServices()
        } else if (id == R.id.rlFamilyServicesDue) {
            openFamilyDueServices()
        } else if (id == R.id.textview_record_reccuring_visit) {
            openFollowUpVisitForm(false)
        } else if (id == R.id.textview_edit) {
            openFollowUpVisitForm(true)
        }
    }

    override fun setupFollowupVisitEditViews(isWithin24Hours: Boolean) {
        if (isWithin24Hours) {
            recordFollowUpVisitLayout!!.visibility = View.GONE
            recordVisitStatusBarLayout!!.visibility = View.VISIBLE;
            tvEditVisit!!.visibility = View.VISIBLE;
        } else {
            tvEditVisit!!.visibility = View.GONE
            recordFollowUpVisitLayout!!.visibility = View.VISIBLE
            recordVisitStatusBarLayout!!.visibility = View.GONE
        }
    }

    override val context: Context
        get() = this

    override fun openMedicalHistory() {
        // TODO :: Open medical history view
    }

    override fun openUpcomingServices() {
        // TODO :: Show upcoming services
    }

    override fun openFamilyDueServices() {
        // TODO :: Show family due services
    }

    override fun openFollowUpVisitForm(isEdit: Boolean) {
        // TODO :: Open follow-up visit form for editing
    }

    override fun setUpComingServicesStatus(
        service: String?,
        status: AlertStatus?,
        date: Date?
    ) {
        showProgressBar(false)
        val dateFormat =
            SimpleDateFormat("dd MMM", Locale.getDefault())
        if (status == AlertStatus.complete) return
        overDueRow!!.visibility = View.VISIBLE
        rlUpcomingServices!!.visibility = View.VISIBLE
        if (status == AlertStatus.upcoming) {
            tvUpComingServices!!.text = fromHtml(
                getString(
                    R.string.tb_upcoming_visit,
                    service,
                    dateFormat.format(date)
                )
            )
        } else {
            tvUpComingServices!!.text = fromHtml(
                getString(
                    R.string.tb_service_due,
                    service,
                    dateFormat.format(date)
                )
            )
        }
    }

    override fun setFamilyStatus(status: AlertStatus?) {
        familyRow!!.visibility = View.VISIBLE
        rlFamilyServicesDue!!.visibility = View.VISIBLE
        when (status) {
            AlertStatus.complete -> {
                tvFamilyStatus!!.text = getString(R.string.family_has_nothing_due)
            }
            AlertStatus.normal -> {
                tvFamilyStatus!!.text = getString(R.string.family_has_services_due)
            }
            AlertStatus.urgent -> {
                tvFamilyStatus!!.text = fromHtml(getString(R.string.family_has_service_overdue))
            }
            else -> tvFamilyStatus!!.text = getString(R.string.family_has_nothing_due)
        }
    }

    override fun setProfileViewDetails(tbMemberObject: TbMemberObject?) {
        val age = Period(
            DateTime(tbMemberObject!!.age),
            DateTime()
        ).years
        tvName!!.text = String.format(
            Locale.getDefault(), "%s %s %s, %d", tbMemberObject.firstName,
            tbMemberObject.middleName, tbMemberObject.lastName, age
        )
        tvGender!!.text = tbMemberObject.gender
        tvLocation!!.text = tbMemberObject.address
        tvUniqueID!!.text = tbMemberObject.uniqueId
        imageRenderHelper.refreshProfileImage(
            tbMemberObject.baseEntityId,
            profileImageView,
            getMemberProfileImageResourceIDentifier()
        )
        tvTbRow!!.text = String.format(
            getString(R.string.tb_client_registered_text),
            getString(R.string.tb_on),
            tbMemberObject.tbRegistrationDate
        )
        if (StringUtils.isNotBlank(tbMemberObject.familyHead) && tbMemberObject.familyHead == tbMemberObject.baseEntityId) {
            findViewById<View>(R.id.tb_family_head).visibility = View.VISIBLE
        }
        if (StringUtils.isNotBlank(tbMemberObject.primaryCareGiver) && tbMemberObject.primaryCareGiver == tbMemberObject.baseEntityId) {
            findViewById<View>(R.id.tb_primary_caregiver).visibility = View.VISIBLE
        }
    }

    private fun formatTime(dateTime: String): CharSequence? {
        var timePassedString: CharSequence? = null
        try {
            val df =
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = df.parse(dateTime)
            timePassedString =
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(date)
        } catch (e: Exception) {
            Timber.d(e)
        }
        return timePassedString
    }

    override fun updateLastVisitRow(lastVisitDate: Date?) {
        showProgressBar(false)
        if (lastVisitDate == null) return
        tvLastVisitDay!!.visibility = View.VISIBLE
        numOfDays = Days.daysBetween(
            DateTime(lastVisitDate).toLocalDate(),
            DateTime().toLocalDate()
        ).days
        tvLastVisitDay!!.text = getString(
            R.string.last_visit_n_days_ago,
            if (numOfDays <= 1) getString(R.string.less_than_twenty_four) else "$numOfDays " + getString(
                R.string.days
            )
        )
        rlLastVisitLayout!!.visibility = View.VISIBLE
        lastVisitRow!!.visibility = View.VISIBLE
    }

    override fun onMemberDetailsReloaded(tbMemberObject: TbMemberObject?) {
        setupViews()
        fetchProfileData()
    }

    override fun setFollowUpButtonDue() {
        showFollowUpVisitButton(true)
        tvRecordTbFollowUp!!.background = resources.getDrawable(R.drawable.record_tb_followup)
    }

    override fun setFollowUpButtonOverdue() {
        showFollowUpVisitButton(true)
        tvRecordTbFollowUp!!.background =
            resources.getDrawable(R.drawable.record_tb_followup_overdue)
    }

    override fun showFollowUpVisitButton(status: Boolean) {
        if (status) tvRecordTbFollowUp!!.visibility =
            View.VISIBLE else tvRecordTbFollowUp!!.visibility = View.GONE
    }

    override fun hideFollowUpVisitButton() {
        tvRecordTbFollowUp!!.visibility = View.GONE
    }

    override fun showProgressBar(status: Boolean) {
        progressBar!!.visibility = if (status) View.VISIBLE else View.GONE
    }

    override fun openTbRegistrationForm() {}

    companion object {
        fun startProfileActivity(activity: Activity, memberObject: TbMemberObject) {
            val intent = Intent(activity, BaseTbProfileActivity::class.java)
            intent.putExtra(Constants.ActivityPayload.MEMBER_OBJECT, memberObject)
            activity.startActivity(intent)
        }
    }
}