package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.base.BaseResponse

data class DictionariesResponse(

    @SerializedName("Data")
    var dictionaries: Dictionaries

) : BaseResponse() {
    data class Dictionaries(

        @SerializedName("TimeKey")
        var timeKey: Long,

        @SerializedName("Nauczyciele")
        var teachers: List<Teacher>,

        @SerializedName("Pracownicy")
        var employees: List<Employee>,

        @SerializedName("Przedmioty")
        var subjects: List<Subject>,

        @SerializedName("PoryLekcji")
        var lessonTimes: List<LessonTime>,

        @SerializedName("KategorieOcen")
        var gradeCategories: List<GradeCategory>,

        @SerializedName("KategorieUwag")
        var noteCategories: List<NoteCategory>,

        @SerializedName("KategorieFrekwencji")
        var attendanceCategories: List<AttendanceCategory>,

        @SerializedName("TypyFrekwencji")
        var attendanceTypes: List<AttendanceType>
    )
}
