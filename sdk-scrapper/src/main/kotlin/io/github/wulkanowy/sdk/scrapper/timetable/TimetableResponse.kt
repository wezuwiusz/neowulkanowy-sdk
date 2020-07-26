package io.github.wulkanowy.sdk.scrapper.timetable

import com.google.gson.annotations.SerializedName
import org.jsoup.nodes.Element
import java.util.Date

class TimetableResponse {

    @SerializedName("Header")
    var _headersOld: List<Header> = emptyList()

    @SerializedName("Headers")
    var headers: List<Header> = emptyList()

    @SerializedName("Rows")
    var rows2api: List<List<String>> = emptyList()

    class Header {

        @SerializedName("Text")
        lateinit var date: String
    }

    class TimetableRow {

        var number: Int = 0

        lateinit var startTime: String

        lateinit var endTime: String

        var lessons: List<TimetableCell> = emptyList()

        class TimetableCell {

            var number: Int = 0

            lateinit var start: Date

            lateinit var end: Date

            lateinit var date: Date

            lateinit var td: Element
        }
    }
}
