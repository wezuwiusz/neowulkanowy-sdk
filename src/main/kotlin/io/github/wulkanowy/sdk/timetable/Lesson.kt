package io.github.wulkanowy.sdk.timetable

import com.google.gson.annotations.SerializedName

data class Lesson(

    @SerializedName("Dzien")
    val day: Long,

    @SerializedName("DzienTekst")
    val dayText: String,

    @SerializedName("NumerLekcji")
    val lessonNumber: Int,

    @SerializedName("IdPoraLekcji")
    val lessonTimeId: Int,

    @SerializedName("IdPrzedmiot")
    val subjectId: Int,

    @SerializedName("PrzedmiotNazwa")
    val subjectName: String,

    @SerializedName("PodzialSkrot")
    val divisionShort: String?,

    @SerializedName("Sala")
    val room: String?,

    @SerializedName("IdPracownik")
    val employeeId: Int,

    @SerializedName("IdPracownikWspomagajacy")
    val employeeSupporterId: Int?,

    @SerializedName("IdPracownikOld")
    val employeeOldId: Int?,

    @SerializedName("IdPracownikWspomagajacyOld")
    val employeeSupporterOldId: Int?,

    @SerializedName("IdPlanLekcji")
    val timetableId: Int,

    @SerializedName("AdnotacjaOZmianie")
    val annotationAboutChange: String,

    @SerializedName("PrzekreslonaNazwa")
    val overriddenName: Boolean,

    @SerializedName("PogrubionaNazwa")
    val boldName: Boolean,

    @SerializedName("PlanUcznia")
    val studentPlan: Boolean
)
