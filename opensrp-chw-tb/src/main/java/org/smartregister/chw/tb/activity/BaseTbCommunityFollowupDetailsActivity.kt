package org.smartregister.chw.tb.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.joda.time.Period
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.util.Constants
import org.smartregister.view.activity.SecuredActivity
import org.smartregister.view.customcontrols.CustomFontTextView
import java.text.SimpleDateFormat
import java.util.*

/***
 * This class is for displaying the referral details for the selected client It extends [SecuredActivity]
 */
open class BaseTbCommunityFollowupDetailsActivity : SecuredActivity(), View.OnClickListener {

    protected lateinit var appBarLayout: AppBarLayout
    private lateinit var clientName: CustomFontTextView
    private lateinit var careGiverName: CustomFontTextView
    private lateinit var careGiverPhone: CustomFontTextView
    private lateinit var comments: CustomFontTextView
    private lateinit var referralDate: CustomFontTextView
    private lateinit var lastFacilityVisitDate: CustomFontTextView
    private lateinit var referralType: CustomFontTextView
    val baseEntityId: String? = null
    private lateinit var locationName: CustomFontTextView
    var memberObject: TbMemberObject? = null
    private lateinit var markAsDone: CustomFontTextView

    override fun onCreateOptionsMenu(menu: Menu) = false

    override fun onCreation() {
        setContentView(R.layout.tb_community_followup_details_activity)
        inflateToolbar()
        memberObject =
            intent.getSerializableExtra(Constants.TbMemberObject.MEMBER_OBJECT) as TbMemberObject
        setUpViews()
    }

    override fun onResumption() = Unit

    private fun inflateToolbar() {
        val toolbar =
            findViewById<Toolbar>(R.id.back_referrals_toolbar)
        val toolBarTextView: CustomFontTextView = toolbar.findViewById(R.id.toolbar_title)
        setSupportActionBar(toolbar)
        supportActionBar?.also {
            it.setDisplayHomeAsUpEnabled(true)
            val upArrow = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)
            upArrow.setColorFilter(resources.getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP)
            it.setHomeAsUpIndicator(upArrow)
            it.elevation = 0f
        }
        toolbar.setNavigationOnClickListener { finish() }
        toolBarTextView.setText(R.string.back_to_referrals)
        toolBarTextView.setOnClickListener { finish() }
        appBarLayout = findViewById(R.id.app_bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) appBarLayout.outlineProvider =
            null
    }

    private fun setUpViews() {
        clientName = findViewById(R.id.client_name)
        careGiverName = findViewById(R.id.care_giver_name)
        careGiverPhone = findViewById(R.id.care_giver_phone)
        comments = findViewById(R.id.comments)
        referralDate = findViewById(R.id.referral_date)
        lastFacilityVisitDate = findViewById(R.id.last_facility_visit_date)
        referralType = findViewById(R.id.referral_type)
        locationName = findViewById(R.id.location_name)
        markAsDone = findViewById(R.id.mark_ask_done)
        markAsDone.setOnClickListener(this)
        obtainReferralDetails()
    }

    @SuppressLint("SetTextI18n")
    private fun obtainReferralDetails() {
        memberObject?.also {
            val clientAge = Period(DateTime(it.age), DateTime()).years.toString()
            clientName.text = "${it.firstName} ${it.middleName} ${it.lastName}, $clientAge"
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val referralDateCalendar = Calendar.getInstance()
            referralDateCalendar.timeInMillis = it.tbCommunityReferralDate?.time ?: 0
            referralDate.text = dateFormatter.format(referralDateCalendar.time)
            locationName.text = it.address

            referralDateCalendar.timeInMillis = it.lastFacilityVisitDate?.time ?: 0
            lastFacilityVisitDate.text = dateFormatter.format(referralDateCalendar.time)

            referralType.text = it.reasonsForIssuingCommunityFollowupReferral
            if (!it.primaryCareGiver.isNullOrEmpty() && clientAge.toInt() < 5)
                careGiverName.text = String.format("CG : %s", it.primaryCareGiver)
            else
                careGiverName.visibility = View.GONE
            careGiverPhone.text =
                if (familyMemberContacts!!.isEmpty() || familyMemberContacts == null) getString(
                    R.string.phone_not_provided
                ) else familyMemberContacts
        }
    }


    private val familyMemberContacts: String?
        get() {
            var phoneNumber: String? = ""
            val familyPhoneNumber = memberObject!!.phoneNumber
            val familyPhoneNumberOther = memberObject!!.otherPhoneNumber
            when {
                StringUtils.isNoneEmpty(familyPhoneNumber) -> {
                    phoneNumber = familyPhoneNumber
                }
                StringUtils.isEmpty(familyPhoneNumber) && StringUtils.isNoneEmpty(
                    familyPhoneNumberOther
                )
                -> {
                    phoneNumber = familyPhoneNumberOther
                }
                StringUtils.isNoneEmpty(familyPhoneNumber) && StringUtils.isNoneEmpty(
                    familyPhoneNumberOther
                )
                -> {
                    phoneNumber = "$familyPhoneNumber, $familyPhoneNumberOther"
                }
                StringUtils.isNoneEmpty(memberObject!!.familyHeadPhoneNumber) -> {
                    phoneNumber = memberObject!!.familyHeadPhoneNumber
                }
            }
            return phoneNumber
        }

    companion object {
        /**
         * This static method is used to launch [BaseTbCommunityFollowupDetailsActivity] activity
         *
         * @param [activity] the activity that you want to launch [BaseTbCommunityFollowupDetailsActivity] from
         * @param [memberObject] entity class for the client with all the required details
         */
        @JvmStatic
        fun startReferralDetailsViewActivity(activity: Activity, memberObject: TbMemberObject?) {
            activity.startActivity(
                Intent(activity, BaseTbCommunityFollowupDetailsActivity::class.java).apply {
                    putExtra(Constants.TbMemberObject.MEMBER_OBJECT, memberObject)
                }
            )
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.mark_ask_done) {
            openFollowupForm()
        }
    }

    protected open fun openFollowupForm() = Unit
}