package org.smartregister.chw.tb.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.nerdstone.neatandroidstepper.core.domain.StepperActions
import com.nerdstone.neatformcore.domain.builders.FormBuilder
import com.nerdstone.neatformcore.domain.model.NFormViewData
import com.nerdstone.neatformcore.form.json.JsonFormBuilder
import com.nerdstone.neatformcore.form.json.JsonFormEmbedded
import org.joda.time.DateTime
import org.joda.time.Period
import org.json.JSONException
import org.json.JSONObject
import org.smartregister.chw.tb.R
import org.smartregister.chw.tb.contract.BaseRegisterFormsContract
import org.smartregister.chw.tb.dao.TbDao
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.interactor.BaseRegisterFormsInteractor
import org.smartregister.chw.tb.presenter.BaseRegisterFormsPresenter
import org.smartregister.chw.tb.util.Constants
import org.smartregister.chw.tb.util.JsonFormConstants
import timber.log.Timber
import java.util.*


/**
 * Created by cozej4 on 2020-05-13.
 *
 * @cozej4 https://github.com/cozej4
 */
/**
 * This is the activity for loading tb registration and followup JSON forms. It implements [BaseRegisterFormsContract.View]
 * and [StepperActions] (which is from the neat form library) that provides callback methods from the
 * form builder. It exposes a method to receiving the data from the views and exiting the activity
 */
open class BaseTbRegistrationFormsActivity : AppCompatActivity(), BaseRegisterFormsContract.View {
    protected var presenter: BaseRegisterFormsContract.Presenter? = null
    protected var baseEntityId: String? = null
    protected var formName: String? = null
    private var formBuilder: FormBuilder? = null
    private var jsonForm: JSONObject? = null
    private var useDefaultNeatFormLayout: Boolean? = null
    private lateinit var formLayout: LinearLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var sampleToolBar: Toolbar
    private lateinit var pageTitleTextView: TextView
    private lateinit var clientNameTitleTextView: TextView
    private lateinit var exitFormImageView: ImageView
    private lateinit var completeButton: ImageView
    var tbMemberObject: TbMemberObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tb_registration)
        mainLayout = findViewById(R.id.mainLayout)
        formLayout = findViewById(R.id.formLayout)
        sampleToolBar = findViewById(R.id.sampleToolBar)
        pageTitleTextView = findViewById(R.id.pageTitleTextView)
        clientNameTitleTextView = findViewById(R.id.clientNameTitleTextView)
        exitFormImageView = findViewById(R.id.exitFormImageView)
        completeButton = findViewById(R.id.completeButton)

        with(this.intent) {
            baseEntityId = getStringExtra(Constants.ActivityPayload.BASE_ENTITY_ID)
            action = getStringExtra(Constants.ActivityPayload.ACTION)
            formName = getStringExtra(Constants.ActivityPayload.TB_REGISTRATION_FORM_NAME)
            useDefaultNeatFormLayout =
                getBooleanExtra(Constants.ActivityPayload.USE_DEFAULT_NEAT_FORM_LAYOUT, true)
            try {
                jsonForm = JSONObject(getStringExtra(Constants.ActivityPayload.JSON_FORM))
            } catch (e: JSONException) {
                Timber.e(e)
            }
            presenter = presenter()

            tbMemberObject =
                if (jsonForm!!.getString(JsonFormConstants.ENCOUNTER_TYPE) == Constants.EventType.TB_COMMUNITY_FOLLOWUP_FEEDBACK) {
                    TbDao.getCommunityFollowupMember(baseEntityId!!)
                } else {
                    TbDao.getMember(baseEntityId!!)
                }
            with(tbMemberObject!!) {
                val age = Period(DateTime(this.age), DateTime()).years
                clientNameTitleTextView.text =
                    "${this.firstName} ${this.middleName} ${this.lastName}, $age"

                pageTitleTextView.text =
                    jsonForm?.getJSONArray("steps")?.getJSONObject(0)?.getString("title") ?: ""
            }

            exitFormImageView.setOnClickListener {
                if (it.id == R.id.exitFormImageView) {
                    AlertDialog.Builder(
                        this@BaseTbRegistrationFormsActivity,
                        R.style.AlertDialogTheme
                    )
                        .setTitle(getString(R.string.confirm_form_close))
                        .setMessage(getString(R.string.confirm_form_close_explanation))
                        .setNegativeButton(R.string.yes) { _: DialogInterface?, _: Int -> finish() }
                        .setPositiveButton(R.string.no) { _: DialogInterface?, _: Int ->
                            Timber.d("Do Nothing exit confirm dialog")
                        }
                        .create()
                        .show()
                }
            }

            completeButton.setOnClickListener {
                if (it.id == R.id.completeButton) {
                    if (formBuilder?.getFormDataAsJson() != "") {

                        val formData = formBuilder!!.getFormData()
                        if (formData.isNotEmpty()) {

                            if (jsonForm!!.getString(JsonFormConstants.ENCOUNTER_TYPE) == Constants.EventType.TB_COMMUNITY_FOLLOWUP_FEEDBACK) {
                                //Saving referral form id
                                formData[JsonFormConstants.TB_COMMUNITY_REFERRAL_FORM_ID] =
                                    NFormViewData().apply {
                                        value = tbMemberObject!!.communityReferralFormId
                                    }
                            }

                            presenter!!.saveForm(formData, jsonForm!!)

                            Toast.makeText(
                                applicationContext,
                                getString(R.string.successful_registration),
                                Toast.LENGTH_LONG
                            ).show()
                            Timber.d("Saved Data = %s", Gson().toJson(formData))
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent);
                            finish()
                        }
                        finish()
                    }
                }
            }

            createViewsFromJson()
        }


    }

    private fun createViewsFromJson() {
        try {
            val customLayouts = ArrayList<View>().also { list ->
                list.add(layoutInflater.inflate(R.layout.tb_registration_form_view, null))
            }

            formBuilder = JsonFormBuilder(jsonForm.toString(), this)
            JsonFormEmbedded(
                formBuilder as JsonFormBuilder,
                formLayout
            ).buildForm(if (useDefaultNeatFormLayout!!) customLayouts else null)


        } catch (ex: JSONException) {
            Timber.e(ex)
        }
    }

    override fun presenter() = BaseRegisterFormsPresenter(
        baseEntityId!!, this, BaseRegisterFormsInteractor()
    )


    override fun setProfileViewWithData() = Unit

}