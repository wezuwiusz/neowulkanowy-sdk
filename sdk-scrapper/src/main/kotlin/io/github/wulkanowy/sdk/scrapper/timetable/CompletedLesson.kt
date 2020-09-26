package io.github.wulkanowy.sdk.scrapper.timetable

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
class CompletedLesson {

    @Json(name = "Data")
    lateinit var date: Date

    @Json(name = "NrLekcji")
    var number: Int = 0

    @Json(name = "Przedmiot")
    var subject: String = ""

    @Json(name = "Temat")
    var topic: String = ""

    @Json(name = "Nauczyciel")
    var teacher: String = ""

    var teacherSymbol: String = ""

    @Json(name = "Zastepstwo")
    var substitution: String = ""

    @Json(name = "Nieobecnosc")
    var absence: String = ""

    @Json(name = "ZasobyPubliczne")
    var resources: String = ""
}
