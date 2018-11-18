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

        @Selector("td:not(:first-of-type)")
        var lessons: List<Attendance> = emptyList()
    }

    @Selector("#idPrzedmiot option")
    var subjects: List<Subject> = emptyList()

    @Selector(".mainContainer > table thead th:not(:first-of-type):not(:last-of-type)")
    var months: List<String> = emptyList()

    @Selector(".mainContainer > table tbody tr")
    var summaryRows: List<AttendanceSummaryRow> = emptyList()

    class AttendanceSummaryRow {

        @Selector("td", index = 0)
        lateinit var name: String

        @Selector("td:not(:first-of-type):not(:last-of-type)", defValue = "0")
        var value: List<String> = emptyList()
    }
}
