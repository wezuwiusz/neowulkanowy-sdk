package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.annotations.SerializedName

data class GradesResponse(

    @SerializedName("IsSrednia")
    val isAverage: Boolean,

    @SerializedName("IsPunkty")
    val isPoints: Boolean,

    @SerializedName("Oceny")
    val gradesWithSubjects: List<Subject>
) {

    data class Subject(

        @SerializedName("WidocznyPrzedmiot")
        val visibleSubject: Boolean = false,

        @SerializedName("Pozycja")
        val order: Int = 0,

        @SerializedName("Przedmiot")
        val name: String = "",

        @SerializedName("Srednia")
        val average: Double = .0,

        @SerializedName("ProponowanaOcenaRoczna")
        val proposed: String? = "",

        @SerializedName("OcenaRoczna")
        val annual: String? = "",

        @SerializedName("SumaPunktow")
        val pointsSum: String? = "",

        @SerializedName("ProponowanaOcenaRocznaPunkty")
        val proposedPoints: String? = "",

        @SerializedName("OcenaRocznaPunkty")
        val finalPoints: String? = "",

        @SerializedName("OcenyCzastkowe")
        val grades: List<Grade> = emptyList()
    )
}
