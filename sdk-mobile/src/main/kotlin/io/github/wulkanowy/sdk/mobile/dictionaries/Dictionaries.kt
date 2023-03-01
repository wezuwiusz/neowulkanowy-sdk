package io.github.wulkanowy.sdk.mobile.dictionaries

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Dictionaries(

    @Json(name = "TimeKey")
    val timeKey: Long,

    @Json(name = "Nauczyciele")
    val teachers: List<Teacher>,

    @Json(name = "Pracownicy")
    val employees: List<Employee>,

    @Json(name = "Przedmioty")
    val subjects: List<Subject>,

    @Json(name = "PoryLekcji")
    val lessonTimes: List<LessonTime>,

    @Json(name = "KategorieOcen")
    val gradeCategories: List<GradeCategory>,

    @Json(name = "KategorieUwag")
    val noteCategories: List<NoteCategory>,

    @Json(name = "KategorieFrekwencji")
    val attendanceCategories: List<AttendanceCategory>,

    @Json(name = "TypyFrekwencji")
    val attendanceTypes: List<AttendanceType>,
)
