package org.smartregister.chw.tb.model

import android.database.sqlite.SQLiteException
import org.smartregister.chw.tb.util.DBConstants
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder
import org.smartregister.domain.Location
import org.smartregister.repository.LocationRepository
import timber.log.Timber

open class BaseRegisterFormModel : AbstractRegisterFormModel() {

    override fun getLocationId(locationName: String?): String? = null

    override val healthFacilities: List<Location>?
        get() = try {
            LocationRepository().allLocations
        } catch (e: SQLiteException) {
            Timber.e(e)
            null
        }


    override fun mainSelect(tableName: String, mainCondition: String): String {
        val queryBuilder = SmartRegisterQueryBuilder()
        queryBuilder.SelectInitiateMainTable(tableName, mainColumns(tableName))
        return queryBuilder.mainCondition(mainCondition)
    }

    open fun mainColumns(tableName: String) = arrayOf(
        tableName + "." + DBConstants.Key.RELATIONAL_ID,
        tableName + "." + DBConstants.Key.BASE_ENTITY_ID,
        tableName + "." + DBConstants.Key.FIRST_NAME,
        tableName + "." + DBConstants.Key.MIDDLE_NAME,
        tableName + "." + DBConstants.Key.LAST_NAME,
        tableName + "." + DBConstants.Key.UNIQUE_ID,
        tableName + "." + DBConstants.Key.GENDER,
        tableName + "." + DBConstants.Key.DOB,
        tableName + "." + DBConstants.Key.DOD
    )

}