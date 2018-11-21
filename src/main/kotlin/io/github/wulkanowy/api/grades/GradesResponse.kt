package io.github.wulkanowy.api.grades

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Selector

class GradesResponse {

    @Selector("#okresyKlasyfikacyjneDropDownList option[selected]", attr = "value")
    var semesterId: Int = 0

    @Selector("#okresyKlasyfikacyjneDropDownList option[selected]")
    var semesterNumber: Int = 0

    @Selector(".ocenySzczegoly-table tbody tr:has(td:nth-of-type(2):not(:contains(Brak ocen)))")
    var grades: List<Grade> = emptyList()

    @SerializedName("Oceny")
    var gradesWithSubjects: List<Subject> = emptyList()

    class Subject {

        @SerializedName("Przedmiot")
        val name: String = ""

        @SerializedName("OcenyCzastkowe")
        val grades: List<Grade> = emptyList()

    }
}
