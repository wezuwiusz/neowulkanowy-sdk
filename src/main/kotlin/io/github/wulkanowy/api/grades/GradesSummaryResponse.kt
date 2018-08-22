package io.github.wulkanowy.api.grades

import pl.droidsonroids.jspoon.annotation.Selector

class GradesSummaryResponse {

    @Selector(".ocenyZwykle-table tbody tr")
    var subjects: List<Summary> = listOf()

}
