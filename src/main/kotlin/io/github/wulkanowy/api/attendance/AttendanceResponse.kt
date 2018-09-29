package io.github.wulkanowy.api.attendance

import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class AttendanceResponse {

    @Selector(".presentData thead th:not(:first-of-type)", regex = "\\s(.*)")
    @Format("dd.MM.yyyy")
    var days: List<Date> = emptyList()

    @Selector(".presentData tbody tr")
    var rows: List<AttendanceRow> = emptyList()

    class AttendanceRow {

        @Selector("td", index = 0)
        var number: Int = 0

        @Selector("td.padding-zero")
        var lessons: List<Attendance> = emptyList()
    }
}
