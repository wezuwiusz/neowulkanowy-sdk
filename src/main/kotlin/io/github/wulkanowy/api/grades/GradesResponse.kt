package io.github.wulkanowy.api.grades

import pl.droidsonroids.jspoon.annotation.Selector

class GradesResponse {

    @Selector("#okresyKlasyfikacyjneDropDownList option[selected]", attr = "value")
    var semesterId: Int = 0

    @Selector("#okresyKlasyfikacyjneDropDownList option[selected]")
    var semesterNumber: Int = 0

    @Selector(".ocenySzczegoly-table tbody tr")
    var grades: List<Grade> = emptyList()
}
