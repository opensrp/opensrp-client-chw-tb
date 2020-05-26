package org.smartregister.chw.tb.activity

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.nerdstone.neatformcore.domain.builders.FormBuilder
import com.nerdstone.neatformcore.form.json.JsonFormBuilder
import org.joda.time.DateTime
import org.joda.time.Period
import org.json.JSONException
import org.json.JSONObject
import org.smartregister.AllConstants
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.contract.BaseTbFollowupContract
import org.smartregister.chw.tb.databinding.ActivityFollowupBinding
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.interactor.BaseReferralFollowupInteractor
import org.smartregister.chw.tb.model.BaseReferralFollowupModel
import org.smartregister.chw.tb.presenter.BaseReferralFollowupPresenter
import org.smartregister.chw.tb.util.Constants
import org.smartregister.chw.tb.util.JsonFormUtils.addFormMetadata
import org.smartregister.chw.tb.util.JsonFormUtils.getFormAsJson
import timber.log.Timber
import java.io.FileNotFoundException

/**
 * Created by cozej4 on 2020-05-13.
 *
 * @cozej4 https://github.com/cozej4
 */

/**
 * The base class for tb followup activity. implements [BaseTbFollowupContract.View]
 */
open class BaseTbCommunityFollowupActivity : AppCompatActivity(), BaseTbFollowupContract.View {

    protected var tbMemberObject: TbMemberObject? = null
    protected var formName: String? = null
    protected var presenter: BaseTbFollowupContract.Presenter? = null
    private var viewModel: BaseReferralFollowupModel? = null
    private var jsonForm: JSONObject? = null
    private var formBuilder: FormBuilder? = null
    private lateinit var textViewName: TextView
    private lateinit var textViewGender: TextView
    private lateinit var textViewLocation: TextView
    private lateinit var textViewUniqueID: TextView
    private lateinit var textViewReferralDate: TextView
    private lateinit var textViewFollowUpReason: TextView
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(this.intent) {
            tbMemberObject =
                getSerializableExtra(Constants.TbMemberObject.MEMBER_OBJECT) as TbMemberObject
            formName = getStringExtra(Constants.ActivityPayload.TB_FOLLOWUP_FORM_NAME)
            try {
                jsonForm =
                    JSONObject(getStringExtra(Constants.ActivityPayload.JSON_FORM))
            } catch (e: JSONException) {
                Timber.e(e)
            }
            tbMemberObject =
                getSerializableExtra(Constants.TbMemberObject.MEMBER_OBJECT) as TbMemberObject
        }

        //initializing the presenter and the viewModel
        presenter = presenter()
        viewModel = ViewModelProviders.of(this).get(presenter!!.getViewModel())
        viewModel?.tbMemberObject = tbMemberObject

        DataBindingUtil.setContentView<ActivityFollowupBinding>(
            this, R.layout.activity_followup
        ).apply { this.viewModel = viewModel }

        setUpViews()

        presenter?.also {
            it.fillProfileData(tbMemberObject)
            it.initializeMemberObject(viewModel?.tbMemberObject!!)
        }

        if (jsonForm == null) {
            try {
                jsonForm = getFormAsJson(formName, this)
            } catch (e: FileNotFoundException) {
                Timber.e(e)
            }
        }

        try {
            addFormMetadata(jsonForm!!, tbMemberObject?.baseEntityId, locationID)
        } catch (e: JSONException) {
            Timber.e(e)
        }

        jsonForm?.also {
            formBuilder = JsonFormBuilder(
                it.toString(), this, findViewById<LinearLayout>(R.id.formLayout)
            ).buildForm(null, null)
        }
    }

    private fun setUpViews() {
        textViewGender = findViewById(R.id.textview_gender)
        textViewName = findViewById(R.id.textview_name)
        textViewLocation = findViewById(R.id.textview_address)
        textViewUniqueID = findViewById(R.id.textview_id)
        textViewReferralDate = findViewById(R.id.referral_date)
        textViewFollowUpReason = findViewById(R.id.followUp_reason)
        buttonSave = findViewById(R.id.save_button)

        val toolbar = findViewById<Toolbar>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.also {
            it.setDisplayHomeAsUpEnabled(true)
            val upArrow = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)
            upArrow.setColorFilter(resources.getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP)
            it.setHomeAsUpIndicator(upArrow)
        }
        toolbar.setNavigationOnClickListener { finish() }

        if (Build.VERSION.SDK_INT >= 21) {
            findViewById<AppBarLayout>(R.id.collapsing_toolbar_appbarlayout).outlineProvider = null
        }
    }

    override fun presenter(): BaseTbFollowupContract.Presenter {
        return BaseReferralFollowupPresenter(
            this, BaseReferralFollowupModel::class.java, BaseReferralFollowupInteractor()
        )
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun setProfileViewWithData() {
        tbMemberObject?.also {
            val ageInYears = Period(DateTime(it.age), DateTime()).years
            textViewLocation.text = it.address
            textViewName.text = "${it.firstName} ${it.middleName} ${it.lastName}, $ageInYears"
            textViewUniqueID.text = it.uniqueId
            textViewGender.text = it.gender
        }
        buttonSave.setOnClickListener {
            presenter!!.saveForm(formBuilder!!.getFormData(), jsonForm!!)
            Timber.e("Saved data = %s", Gson().toJson(formBuilder!!.getFormData()))
        }
    }

    override fun onFollowupSaved() = Unit

    private val locationID
        get() = org.smartregister.Context.getInstance().allSharedPreferences()
            .getPreference(AllConstants.CURRENT_LOCATION_ID)
}