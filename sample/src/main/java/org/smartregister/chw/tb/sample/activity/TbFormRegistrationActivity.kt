package org.smartregister.chw.tb.sample.activity

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.nerdstone.neatformcore.form.json.JsonFormBuilder
import com.nerdstone.neatformcore.form.json.JsonFormEmbedded
import org.smartregister.chw.tb.R

class TbFormRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.neat_tb_registration_form_activity)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.sampleToolBar)
        setSupportActionBar(toolbar)
        val jsonFormBuilder = JsonFormBuilder(this, "json.form/tb_followup_visit.json")
        JsonFormEmbedded(jsonFormBuilder, findViewById<LinearLayout>(R.id.formLayout)).buildForm()
    }
}