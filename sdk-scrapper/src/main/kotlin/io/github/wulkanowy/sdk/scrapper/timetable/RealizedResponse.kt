package io.github.wulkanowy.sdk.scrapper.timetable

import pl.droidsonroids.jspoon.annotation.Selector

class RealizedResponse {

    @Selector(".mainContainer h2, .mainContainer article")
    var items: List<CompletedLesson> = emptyList()
}
