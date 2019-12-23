package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Selector

class GradesResponse {

    @Selector(".ocenySzczegoly-table tbody tr:has(td:nth-of-type(2):not(:contains(Brak ocen)))")
    var grades: List<Grade> = emptyList()

    @SerializedName("IsSrednia")
    var isAverage: Boolean = false

    @SerializedName("IsPunkty")
    var isPoints: Boolean = false

    @SerializedName("Oceny")
    var gradesWithSubjects: List<Subject> = emptyList()

    class Subject {

        @SerializedName("WidocznyPrzedmiot")
        val visibleSubject: Boolean = false

        @SerializedName("Pozycja")
        val order: Int = 0

        @SerializedName("Przedmiot")
        val name: String = ""

        @SerializedName("Srednia")
        val average: Double = .0

        @SerializedName("ProponowanaOcenaRoczna")
        var proposed: String? = ""

        @SerializedName("OcenaRoczna")
        var annual: String? = ""

        @SerializedName("SumaPunktow")
        val pointsSum: String? = ""

        @SerializedName("ProponowanaOcenaRocznaPunkty")
        val proposedPoints: String? = ""

        @SerializedName("OcenaRocznaPunkty")
        val finalPoints: String? = ""

        @SerializedName("OcenyCzastkowe")
        val grades: List<Grade> = emptyList()
    }
}
