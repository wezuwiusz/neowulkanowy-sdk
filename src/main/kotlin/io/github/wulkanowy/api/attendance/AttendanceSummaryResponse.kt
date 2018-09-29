package io.github.wulkanowy.api.attendance

import pl.droidsonroids.jspoon.annotation.Selector

class AttendanceSummaryResponse {

    @Selector(".mainContainer > table thead th:not(:first-of-type):not(:last-of-type)")
    var days: List<String> = emptyList()

    @Selector(".mainContainer > table tbody tr")
    var rows: List<AttendanceSummaryResponse.AttendanceRow> = emptyList()

    class AttendanceRow {

        @Selector("td", index = 0)
        lateinit var name: String

        @Selector("td:not(:first-of-type):not(:last-of-type)", defValue = "0")
        var value: List<String> = emptyList()
    }
}
