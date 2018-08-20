package io.github.wulkanowy.api.grades

import pl.droidsonroids.jspoon.annotation.Selector

class GradesResponse {

    @Selector(".ocenySzczegoly-table tbody tr")
    lateinit var grades: List<Grade>
}
