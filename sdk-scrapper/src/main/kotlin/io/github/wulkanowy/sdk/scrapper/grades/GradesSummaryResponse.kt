package io.github.wulkanowy.sdk.scrapper.grades

import pl.droidsonroids.jspoon.annotation.Selector

class GradesSummaryResponse {

    @Selector(".ocenyZwykle-table tbody tr")
    var subjects: List<GradeSummary> = listOf()
}
