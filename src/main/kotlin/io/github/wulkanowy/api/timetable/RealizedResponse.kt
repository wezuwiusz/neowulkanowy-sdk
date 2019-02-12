package io.github.wulkanowy.api.timetable

import io.github.wulkanowy.api.timetable.CompletedLesson
import pl.droidsonroids.jspoon.annotation.Selector

class RealizedResponse {

    @Selector(".mainContainer h2, .mainContainer article")
    var items: List<CompletedLesson> = emptyList()
}
