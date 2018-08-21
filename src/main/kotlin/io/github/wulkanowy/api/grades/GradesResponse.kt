package io.github.wulkanowy.api.grades

import pl.droidsonroids.jspoon.annotation.Selector

class GradesResponse {

    @Selector(".ocenySzczegoly-table tbody tr")
    var grades: List<Grade> = listOf()
}
