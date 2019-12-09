package io.github.wulkanowy.sdk.scrapper.timetable

import com.google.gson.annotations.SerializedName
import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.Date

class TimetableResponse {

    @Selector(".presentData thead th:not(:nth-of-type(1)):not(:nth-of-type(2))", regex = "\\s(.*)", defValue = "01.01.1970")
    @Format("dd.MM.yyyy")
    var days: List<Date> = emptyList()

    @SerializedName("Headers")
    var header: List<Header> = emptyList()

    @SerializedName("Rows")
    var rows2api: List<List<String>> = emptyList()

    class Header {

        @SerializedName("Text")
        lateinit var date: String
    }

    @Selector(".presentData tbody tr")
    var rows: List<TimetableRow> = emptyList()

    class TimetableRow {

        @Selector("td", index = 0)
        var number: Int = 0

        @Selector("td", index = 1, regex = "(^\\S*)")
        lateinit var startTime: String

        @Selector("td", index = 1, regex = "(\\S*\$)")
        lateinit var endTime: String

        @Selector("td:not(:nth-of-type(1)):not(:nth-of-type(2))")
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
