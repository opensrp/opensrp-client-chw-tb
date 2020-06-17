package org.smartregister.chw.tb.dao

import android.database.Cursor
import org.smartregister.chw.anc.domain.Visit
import org.smartregister.chw.tb.domain.TbAlertObject
import org.smartregister.chw.tb.domain.TbMemberObject
import org.smartregister.chw.tb.util.DBConstants
import org.smartregister.dao.AbstractDao
import org.smartregister.dao.AbstractDao.DataMap
import timber.log.Timber
import java.math.BigDecimal
import java.util.*

object TbDao : AbstractDao() {
    val dataMap =
        DataMap { cursor: Cursor? ->
            val memberObject = TbMemberObject(null)
            memberObject.firstName = getCursorValue(cursor, DBConstants.Key.FIRST_NAME, "")
            memberObject.middleName = getCursorValue(cursor, DBConstants.Key.MIDDLE_NAME, "")
            memberObject.lastName = getCursorValue(cursor, DBConstants.Key.LAST_NAME, "")
            memberObject.address = getCursorValue(cursor, DBConstants.Key.VILLAGE_TOWN)!!
            memberObject.gender = getCursorValue(cursor, DBConstants.Key.GENDER)!!
            memberObject.uniqueId = getCursorValue(cursor, DBConstants.Key.UNIQUE_ID, "")
            memberObject.age = getCursorValue(cursor, DBConstants.Key.DOB)!!
            memberObject.familyBaseEntityId =
                getCursorValue(cursor, DBConstants.Key.FAMILY_BASE_ENTITY_ID, "")
            memberObject.relationalId =
                getCursorValue(cursor, DBConstants.Key.FAMILY_BASE_ENTITY_ID, "")
            memberObject.primaryCareGiver =
                getCursorValue(cursor, DBConstants.Key.PRIMARY_CARE_GIVER)
            memberObject.familyName = getCursorValue(cursor, DBConstants.Key.FAMILY_NAME, "")
            memberObject.phoneNumber = getCursorValue(cursor, DBConstants.Key.PHONE_NUMBER, "")
            memberObject.baseEntityId =
                getCursorValue(cursor, DBConstants.Key.BASE_ENTITY_ID, "")
            memberObject.familyHead = getCursorValue(cursor, DBConstants.Key.FAMILY_HEAD, "")
            memberObject.familyHeadPhoneNumber =
                getCursorValue(cursor, DBConstants.Key.FAMILY_HEAD_PHONE_NUMBER, "")
            memberObject.communityReferralFormId =
                getCursorValue(cursor, DBConstants.Key.COMMUNITY_REFERRAL_FORM_ID, "")
            memberObject.tbRegistrationDate =
                Date(
                    BigDecimal(
                        getCursorValue(
                            cursor,
                            DBConstants.Key.TB_REGISTRATION_DATE,"0"
                        )
                    ).toLong()
                )
            memberObject.communityClientTbRegistrationNumber =
                getCursorValue(
                    cursor,
                    DBConstants.Key.COMMUNITY_CLIENT_TB_REGISTRATION_NUMBER,""
                )
            memberObject.tbCommunityReferralDate =
                Date(
                    BigDecimal(
                        getCursorValue(cursor, DBConstants.Key.TB_COMMUNITY_REFERRAL_DATE,"0")
                    ).toLong()
                )

            memberObject.lastFacilityVisitDate =
                Date(
                    BigDecimal(
                        getCursorValue(cursor, DBConstants.Key.LAST_FACILITY_VISIT_DATE,"0")
                    ).toLong()
                )
            memberObject.reasonsForIssuingCommunityFollowupReferral =
                getCursorValue(cursor, DBConstants.Key.REASONS_FOR_ISSUING_COMMUNITY_REFERRAL, "")
            memberObject.comment =
                getCursorValue(cursor, DBConstants.Key.COMMENTS, "")
            memberObject.clientTbStatusDuringRegistration =
                getCursorValue(cursor, DBConstants.Key.CLIENT_TB_STATUS_DURING_REGISTRATION, "")
            memberObject.clientTbStatusAfterTesting =
                getCursorValue(cursor, DBConstants.Key.CLIENT_TB_STATUS_AFTER_TESTING, "")
            memberObject.isClosed =
                getCursorIntValue(cursor, DBConstants.Key.IS_CLOSED, 0) == 1
            var familyHeadName =
                (getCursorValue(cursor, "family_head_first_name", "") + " "
                        + getCursorValue(cursor, "family_head_middle_name", ""))
            familyHeadName =
                (familyHeadName.trim { it <= ' ' } + " " + getCursorValue(
                    cursor, "family_head_last_name", ""
                )).trim { it <= ' ' }
            memberObject.familyHead = familyHeadName

            val entityType = getCursorValue(cursor, "entity_type", "")
            var familyPcgName = ""
            if (entityType == "ec_independent_client") {
                familyPcgName =
                    (getCursorValue(cursor, "primary_caregiver_name", ""))
                memberObject.primaryCareGiverPhoneNumber =
                    getCursorValue(cursor, DBConstants.Key.OTHER_PHONE_NUMBER, "")
            } else {
                familyPcgName =
                    (getCursorValue(cursor, "pcg_first_name", "") + " "
                            + getCursorValue(cursor, "pcg_middle_name", ""))
                familyPcgName =
                    (familyPcgName.trim { it <= ' ' } + " " + getCursorValue(
                        cursor,
                        "pcg_last_name",
                        ""
                    )).trim { it <= ' ' }
                memberObject.primaryCareGiverPhoneNumber =
                    getCursorValue(cursor, DBConstants.Key.PRIMARY_CARE_GIVER_PHONE_NUMBER, "")
            }

            memberObject.primaryCareGiver = familyPcgName
            memberObject.familyMemberEntityType =
                getCursorValue(cursor, DBConstants.Key.FAMILY_MEMBER_ENTITY_TYPE, "")
            memberObject
        }

    @JvmStatic
    fun isRegisteredForTb(baseEntityID: String?): Boolean {
        val sql =
            """select count(ec_tb_register.base_entity_id) count
               from ec_tb_register
               where base_entity_id = '${baseEntityID}'
               and tb_case_closure_date IS NULL and
               and ec_tb_register.is_closed = 0
            """

        val dataMap = DataMap { cursor: Cursor? -> getCursorIntValue(cursor, "count") }
        val res = readData(sql, dataMap)
        return if (res == null || res.size != 1) false else res[0]!! > 0
    }

    @JvmStatic
    fun getLatestVisit(baseEntityId: String, visitType: String): Visit? {
        val sql =
            """select visit_id, visit_type, parent_visit_id, visit_date from visits 
                WHERE 
                base_entity_id = '${baseEntityId}' and 
                visit_type = '${visitType}' 
                ORDER BY visit_date DESC LIMIT 1"""
        val visit = readData(sql, visitDataMap)
        return if (visit.size == 0) null else visit[0]
    }

    @JvmStatic
    fun getMember(baseEntityID: String): TbMemberObject? {
        val sql =
            """select m.base_entity_id , m.unique_id , m.relational_id as family_base_entity_id , m.dob , m.first_name , 
                    m.middle_name , m.last_name , m.gender , m.phone_number , m.other_phone_number ,  m.entity_type, m.has_primary_caregiver, m.has_primary_caregiver, m.primary_caregiver_name,
                    f.first_name family_name ,f.primary_caregiver , f.family_head , f.village_town ,
                    fh.first_name family_head_first_name , fh.middle_name family_head_middle_name , 
                    fh.last_name family_head_last_name, fh.phone_number family_head_phone_number, 
                    pcg.first_name pcg_first_name , pcg.last_name pcg_last_name , pcg.middle_name pcg_middle_name , 
                    pcg.phone_number  pcg_phone_number , mr.* 
                    from ec_family_member m 
                    inner join ec_family f on m.relational_id = f.base_entity_id 
                    left join ec_tb_register mr on mr.base_entity_id = m.base_entity_id 
                    left join ec_family_member fh on fh.base_entity_id = f.family_head 
                    left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver 
                    where m.base_entity_id ='${baseEntityID}' """

        val sqlHf =
            """select m.base_entity_id , m.unique_id , m.relational_id as family_base_entity_id , m.dob , m.first_name , 
                    m.middle_name , m.last_name , m.gender , m.phone_number , m.other_phone_number ,  m.entity_type,
                    f.first_name family_name ,f.primary_caregiver , f.family_head , f.village_town ,
                    fh.first_name family_head_first_name , fh.middle_name family_head_middle_name , 
                    fh.last_name family_head_last_name, fh.phone_number family_head_phone_number, 
                    pcg.first_name pcg_first_name , pcg.last_name pcg_last_name , pcg.middle_name pcg_middle_name , 
                    pcg.phone_number  pcg_phone_number , mr.* 
                    from ec_family_member m 
                    inner join ec_family f on m.relational_id = f.base_entity_id 
                    left join ec_tb_register mr on mr.base_entity_id = m.base_entity_id 
                    left join ec_family_member fh on fh.base_entity_id = f.family_head 
                    left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver 
                    where m.base_entity_id ='${baseEntityID}' """

        var res = readData(sql, dataMap)
        if (res == null)
            res = readData(sqlHf, dataMap)
        return if (res == null || res.size != 1) null else res[0]
    }

    @JvmStatic
    fun getCommunityFollowupMember(baseEntityID: String): TbMemberObject? {
        val sql =
            """select m.base_entity_id , m.unique_id , m.relational_id as family_base_entity_id , m.dob , m.first_name , 
                    m.middle_name , m.last_name , m.gender , m.phone_number , m.other_phone_number ,  m.entity_type, m.has_primary_caregiver, m.has_primary_caregiver, m.primary_caregiver_name,
                    f.first_name family_name ,f.primary_caregiver , f.family_head , f.village_town ,
                    fh.first_name family_head_first_name , fh.middle_name family_head_middle_name , 
                    fh.last_name family_head_last_name, fh.phone_number family_head_phone_number, 
                    pcg.first_name pcg_first_name , pcg.last_name pcg_last_name , pcg.middle_name pcg_middle_name , 
                    pcg.phone_number  pcg_phone_number , mr.entity_id as base_entity_id, mr.base_entity_id as community_referral_form_id, 
                    mr.last_client_visit_date, mr.tb_community_referral_date, mr.comment, mr.reasons_for_issuing_community_referral, mr.last_interacted_with
                    from ec_family_member m 
                    inner join ec_family f on m.relational_id = f.base_entity_id 
                    inner join ec_tb_community_followup mr on mr.entity_id = m.base_entity_id 
                    left join ec_family_member fh on fh.base_entity_id = f.family_head 
                    left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver 
                    where mr.base_entity_id ='${baseEntityID}' OR mr.entity_id ='${baseEntityID}' """

        val res = readData(sql, dataMap)
        return if (res == null || res.size != 1) null else res[0]
    }

    @JvmStatic
    fun getTbVisitsMedicalHistory(baseEntityId: String): List<Visit>? {
        val sql =
            """SELECT visit_date, visit_id,visit_type, parent_visit_id
               FROM Visits v
               INNER JOIN ec_tb_register tb on tb.base_entity_id = v.base_entity_id
               WHERE v.base_entity_id = '${baseEntityId}' COLLATE NOCASE
                    AND strftime('%Y%d%m', (datetime(v.visit_date/1000, 'unixepoch')))  >= substr(tb.tb_registration_date,7,4) || substr(tb.tb_registration_date,4,2) || substr(tb.tb_registration_date,1,2)
               ORDER BY v.visit_date DESC"""


        val visits = readData(sql, visitDataMap)
        return visits ?: ArrayList()
    }

    private val visitDataMap: DataMap<Visit>
        get() = DataMap { c: Cursor? ->
            val visit = Visit()
            visit.visitId = getCursorValue(c, "visit_id")
            visit.parentVisitID = getCursorValue(c, "parent_visit_id")
            visit.visitType = getCursorValue(c, "visit_type")
            visit.date = Date(BigDecimal(getCursorValue(c, "visit_date")).toLong())

            visit
        }

    @JvmStatic
    fun getTbDetails(baseEntityID: String): List<TbAlertObject?>? {
        val sql =
            "select tb_registration_date from ec_tb_register where base_entity_id = '" + baseEntityID + "' " +
                    "and is_closed is 0"
        val tbAlertObjects: List<TbAlertObject?>? =
            readData(sql, getVisitDetailsDataMap())
        return if (tbAlertObjects!!.isEmpty()) {
            null
        } else tbAlertObjects
    }

    private fun getVisitDetailsDataMap(): DataMap<TbAlertObject>? {
        return DataMap<TbAlertObject> { c: Cursor? ->
            val tbAlertObject = TbAlertObject()
            try {
                tbAlertObject.tbStartDate = getCursorValue(c, "tb_registration_date")
            } catch (e: Exception) {
                Timber.e(e.toString())
            }
            tbAlertObject
        }
    }

    @JvmStatic
    fun getSyncLocationId(baseEntityId: String?): String? {
        val sql = String.format(
            "SELECT sync_location_id FROM ec_family_member WHERE base_entity_id = '%s'",
            baseEntityId
        )
        val dataMap =
            DataMap { cursor: Cursor? ->
                getCursorValue(
                    cursor,
                    "sync_location_id"
                )
            }
        val res =
            readData(sql, dataMap)
        return if (res == null || res.size != 1) null else res[0]
    }
}