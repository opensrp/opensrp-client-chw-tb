package org.smartregister.chw.tb.util

import com.nerdstone.neatformcore.domain.model.NFormViewData
import com.nerdstone.neatformcore.views.containers.MultiChoiceCheckBox
import com.nerdstone.neatformcore.views.widgets.SpinnerNFormView
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.smartregister.util.JsonFormUtils.*

class JsonFormUtilsTest {

    @Test
    fun `Should add form metadata to the provided json`() {
        val jsonObject = JSONObject().put("metadata", JSONObject())
        val jsonObjectMetadata = jsonObject.getJSONObject("metadata")
        JsonFormUtils.addFormMetadata(jsonObject, "base_entity_id", "current_location_id")
        assertEquals(jsonObjectMetadata.getString(ENTITY_ID), "base_entity_id")
        assertEquals(jsonObjectMetadata.getString(ENCOUNTER_LOCATION), "current_location_id")
    }

    @Test
    fun `Should return the right obs objects for the event`() {
        val mapOfValues = getValues()
        val list = JsonFormUtils.getObs(mapOfValues)
        assertTrue(list.size == 2)
        val ob1 = list[0]
        assertEquals(ob1.fieldType, "1")
        assertEquals(ob1.formSubmissionField, "spinner_radio_texts")
        assertEquals(ob1.fieldCode, "id1")
        assertEquals(ob1.parentCode, "p1")
        assertTrue(ob1.humanReadableValues.contains("Nairobi"))
        assertTrue(ob1.values.contains("opid1"))
        val ob2 = list[1]
        assertEquals(ob2.fieldType, "2")
        assertEquals(ob2.formSubmissionField, "multiple_checkboxes")
        assertEquals(ob2.fieldCode, "id2")
        assertEquals(ob2.parentCode, "p2")
        assertTrue(ob2.values.contains("opid21") && ob2.values.contains("opid22"))
        assertTrue(
            ob2.humanReadableValues.contains("Option 1") && ob2.humanReadableValues.contains(
                "Option 2"
            )
        )
    }

    private fun getValues(): HashMap<String, NFormViewData> {
        return hashMapOf<String, NFormViewData>().also { values ->
            values["spinner_radio_texts"] = NFormViewData().apply {
                type = SpinnerNFormView::class.simpleName
                metadata = hashMapOf(
                    OPENMRS_ENTITY to "1",
                    OPENMRS_ENTITY_ID to "id1",
                    OPENMRS_ENTITY_PARENT to "p1"
                )
                value = NFormViewData().apply {
                    type = null
                    value = "Nairobi"
                    metadata = hashMapOf(
                        OPENMRS_ENTITY to "op1",
                        OPENMRS_ENTITY_ID to "opid1",
                        OPENMRS_ENTITY_PARENT to "opp1"
                    )
                }
            }
            values["multiple_checkboxes"] = NFormViewData().apply {
                type = MultiChoiceCheckBox::class.simpleName
                metadata = hashMapOf(
                    OPENMRS_ENTITY to "2",
                    OPENMRS_ENTITY_ID to "id2",
                    OPENMRS_ENTITY_PARENT to "p2"
                )
                value = hashMapOf<String, NFormViewData>().also {
                    it["opt1"] = NFormViewData().apply {
                        type = null
                        value = "Option 1"
                        metadata = hashMapOf(
                            OPENMRS_ENTITY to "op21",
                            OPENMRS_ENTITY_ID to "opid21",
                            OPENMRS_ENTITY_PARENT to "opp21"
                        )
                    }
                    it["opt2"] = NFormViewData().apply {
                        type = null
                        value = "Option 2"
                        metadata = hashMapOf(
                            OPENMRS_ENTITY to "op22",
                            OPENMRS_ENTITY_ID to "opid22",
                            OPENMRS_ENTITY_PARENT to "opp22"
                        )
                    }
                }
            }
        }
    }
}