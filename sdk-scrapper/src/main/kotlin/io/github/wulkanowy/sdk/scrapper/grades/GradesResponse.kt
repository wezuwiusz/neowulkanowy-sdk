package io.github.wulkanowy.sdk.scrapper.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradesResponse(

    @Json(name = "IsSrednia")
    val isAverage: Boolean,

    @Json(name = "IsPunkty")
    val isPoints: Boolean,

    @Json(name = "Oceny")
    val gradesWithSubjects: List<Subject>
) {

    @JsonClass(generateAdapter = true)
    data class Subject(

        @Json(name = "WidocznyPrzedmiot")
        val visibleSubject: Boolean = false,

        @Json(name = "Pozycja")
        val order: Int = 0,

        @Json(name = "Przedmiot")
        val name: String = "",

        @Json(name = "Srednia")
        val average: Double = .0,

        @Json(name = "ProponowanaOcenaRoczna")
        val proposed: String? = "",

        @Json(name = "OcenaRoczna")
        val annual: String? = "",

        @Json(name = "SumaPunktow")
        val pointsSum: String? = "",

        @Json(name = "ProponowanaOcenaRocznaPunkty")
        val proposedPoints: String? = "",

        @Json(name = "OcenaRocznaPunkty")
        val finalPoints: String? = "",

        @Json(name = "OcenyCzastkowe")
        val grades: List<Grade> = emptyList()
    )
}
