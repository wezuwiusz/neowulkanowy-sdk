package io.github.wulkanowy.api.grades

import pl.droidsonroids.jspoon.annotation.Selector

class GradesStatisticsResponse {

    @Selector("#okresyKlasyfikacyjneDropDownList option[selected]", attr = "value")
    var semesterId: Int = 0

    @Selector(".mainContainer > div table tbody tr")
    var items: List<GradeStatistics> = emptyList()
}
