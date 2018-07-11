package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class Dictionaries(

        @SerializedName("TimeKey")
        val timeKey: Long,

        @SerializedName("Nauczyciele")
        val teachers: List<Teacher>,

        @SerializedName("Pracownicy")
        val employees: List<Employee>,

        @SerializedName("Przedmioty")
        val subjects: List<Subject>,

        @SerializedName("PoryLekcji")
        val lessonTimes: List<LessonTime>,

        @SerializedName("KategorieOcen")
        val gradeCategories: List<GradeCategory>,

        @SerializedName("KategorieUwag")
        val noteCategories: List<NoteCategory>,

        @SerializedName("KategorieFrekwencji")
        val attendanceCategories: List<AttendanceCategory>,

        @SerializedName("TypyFrekwencji")
        val attendanceTypes: List<AttendanceType>
)
