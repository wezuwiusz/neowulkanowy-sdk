package io.github.wulkanowy.sdk.mobile.timetable

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Lesson(

    @Json(name = "Dzien")
    val day: Long,

    @Json(name = "DzienTekst")
    val dayText: String,

    @Json(name = "NumerLekcji")
    val lessonNumber: Int,

    @Json(name = "IdPoraLekcji")
    val lessonTimeId: Int,

    @Json(name = "IdPrzedmiot")
    val subjectId: Int,

    @Json(name = "PrzedmiotNazwa")
    val subjectName: String,

    @Json(name = "PodzialSkrot")
    val divisionShort: String?,

    @Json(name = "Sala")
    val room: String?,

    @Json(name = "IdPracownik")
    val employeeId: Int,

    @Json(name = "IdPracownikWspomagajacy")
    val employeeSupporterId: Int?,

    @Json(name = "IdPracownikOld")
    val employeeOldId: Int?,

    @Json(name = "IdPracownikWspomagajacyOld")
    val employeeSupporterOldId: Int?,

    @Json(name = "IdPlanLekcji")
    val timetableId: Int,

    @Json(name = "AdnotacjaOZmianie")
    val annotationAboutChange: String?,

    @Json(name = "PrzekreslonaNazwa")
    val overriddenName: Boolean,

    @Json(name = "PogrubionaNazwa")
    val boldName: Boolean,

    @Json(name = "PlanUcznia")
    val studentPlan: Boolean
)
