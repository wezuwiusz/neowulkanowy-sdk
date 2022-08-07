package io.github.wulkanowy.sdk.scrapper.grades

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GradesResponse(

    @SerialName("IsSrednia")
    val isAverage: Boolean,

    @SerialName("IsPunkty")
    val isPoints: Boolean,

    @SerialName("IsDlaDoroslych")
    val isForAdults: Boolean,

    @SerialName("TypOcen")
    val type: Int,

    @SerialName("Oceny")
    val gradesWithSubjects: List<Subject>
) {

    @Serializable
    data class Subject(

        @SerialName("WidocznyPrzedmiot")
        val visibleSubject: Boolean = false,

        @SerialName("Pozycja")
        val order: Int = 0,

        @SerialName("Przedmiot")
        val name: String = "",

        @SerialName("Srednia")
        val average: Double = .0,

        @SerialName("ProponowanaOcenaRoczna")
        val proposed: String? = "",

        @SerialName("OcenaRoczna")
        val annual: String? = "",

        @SerialName("SumaPunktow")
        val pointsSum: String? = "",

        @SerialName("ProponowanaOcenaRocznaPunkty")
        val proposedPoints: String? = "",

        @SerialName("OcenaRocznaPunkty")
        val finalPoints: String? = "",

        @SerialName("OcenyCzastkowe")
        val grades: List<Grade> = emptyList()
    )
}
