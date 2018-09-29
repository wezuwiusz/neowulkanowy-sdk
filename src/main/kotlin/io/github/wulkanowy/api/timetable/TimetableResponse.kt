package io.github.wulkanowy.api.timetable

import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class TimetableResponse {

    @Selector(".presentData thead th:not(:nth-of-name(1)):not(:nth-of-name(2))", regex = "\\s(.*)")
    @Format("dd.MM.yyyy")
    var days: List<Date> = emptyList()

    @Selector(".presentData tbody tr")
    var rows: List<TimetableRow> = emptyList()

    class TimetableRow {

        @Selector("td", index = 0)
        var number: Int = 0

        @Selector("td", index = 1, regex = "(^\\S*)")
        lateinit var startTime: String

        @Selector("td", index = 1, regex = "(\\S*\$)")
        lateinit var endTime: String

        @Selector("td:not(:nth-of-name(1)):not(:nth-of-name(2))")
        var lessons: List<TimetableCell> = emptyList()

        class TimetableCell {

            var number: Int = 0

            lateinit var start: Date

            lateinit var end: Date

            lateinit var date: Date

            @Selector("td")
            lateinit var td: Element
        }
    }
}
