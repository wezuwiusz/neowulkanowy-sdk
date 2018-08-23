package io.github.wulkanowy.api.grades

import pl.droidsonroids.jspoon.annotation.Selector

class GradesResponse {

    @Selector("#okresyKlasyfikacyjneDropDownList option[selected]", attr = "value")
    lateinit var semesterId: String

    @Selector("#okresyKlasyfikacyjneDropDownList option[selected]")
    lateinit var semesterNumber: String

    @Selector(".ocenySzczegoly-table tbody tr")
    var grades: List<Grade> = emptyList()
}
