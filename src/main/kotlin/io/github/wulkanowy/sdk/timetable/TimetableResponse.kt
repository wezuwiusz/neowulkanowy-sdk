package io.github.wulkanowy.sdk.timetable

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.base.BaseResponse

data class TimetableResponse(

    @SerializedName("Data")
    var lessons: List<Lesson>

) : BaseResponse() {

    data class Lesson(

        @SerializedName("Dzien")
        var day: Long,

        @SerializedName("DzienTekst")
        var dayText: String,

        @SerializedName("NumerLekcji")
        var lessonNumber: Int,

        @SerializedName("IdPoraLekcji")
        var lessonTimeId: Int,

        @SerializedName("IdPrzedmiot")
        var subjectId: Int,

        @SerializedName("PrzedmiotNazwa")
        var subjectName: String,

        @SerializedName("PodzialSkrot")
        var divisionShort: String,

        @SerializedName("Sala")
        var room: String,

        @SerializedName("IdPracownik")
        var employeeId: Int,

        @SerializedName("IdPracownikWspomagajacy")
        var employeeSupporterId: Int,

        @SerializedName("IdPracownikOld")
        var employeeOldId: Int,

        @SerializedName("IdPracownikWspomagajacyOld")
        var employeeSupporterOldId: Int,

        @SerializedName("IdPlanLekcji")
        var timetableId: Int,

        @SerializedName("AdnotacjaOZmianie")
        var annotationAboutChange: String,

        @SerializedName("PrzekreslonaNazwa")
        var overriddenName: Boolean,

        @SerializedName("PogrubionaNazwa")
        var boldName: Boolean,

        @SerializedName("PlanUcznia")
        var studentPlan: Boolean
    )
}
