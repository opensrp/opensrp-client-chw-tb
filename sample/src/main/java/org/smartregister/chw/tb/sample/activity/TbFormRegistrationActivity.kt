package org.smartregister.chw.tb.sample.activity

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.nerdstone.neatformcore.form.json.JsonFormBuilder
import org.smartregister.chw.tb.R

class TbFormRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.neat_tb_registration_form_activity)
        JsonFormBuilder(
            this, "json.form/tb_registration_form.json",
            findViewById<LinearLayout>(R.id.formLayout)
        ).buildForm(null, null)
    }
}