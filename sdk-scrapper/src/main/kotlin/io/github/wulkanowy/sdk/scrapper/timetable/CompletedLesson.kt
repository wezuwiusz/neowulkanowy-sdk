package io.github.wulkanowy.sdk.scrapper.timetable

import com.google.gson.annotations.SerializedName
import java.util.Date

class CompletedLesson {

    @SerializedName("Data")
    lateinit var date: Date

    @SerializedName("NrLekcji")
    var number: Int = 0

    @SerializedName("Przedmiot")
    var subject: String = ""

    @SerializedName("Temat")
    var topic: String = ""

    @SerializedName("Nauczyciel")
    var teacher: String = ""

    var teacherSymbol: String = ""

    @SerializedName("Zastepstwo")
    var substitution: String = ""

    @SerializedName("Nieobecnosc")
    var absence: String = ""

    @SerializedName("ZasobyPubliczne")
    var resources: String = ""
}
