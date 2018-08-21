package io.github.wulkanowy.api.attendance

import io.github.wulkanowy.api.DATE_FORMAT
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class AttendanceResponse {

    @Selector(".presentData thead th:not(:first-of-type)", regex = "\\s(.*)")
    @Format(value = DATE_FORMAT)
    var days: List<Date> = emptyList()

    @Selector(".presentData tbody tr")
    var rows: List<AttendanceRow> = emptyList()

    class AttendanceRow {

        @Selector("td", index = 0)
        var number: Int = 0

        @Selector("td:not(:first-of-type)")
        var lessons: List<Attendance> = emptyList()
    }
}
