package io.github.wulkanowy.sdk.scrapper.timetable

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.jsoup.nodes.Element
import java.util.Date

@JsonClass(generateAdapter = true)
class TimetableResponse {

    @Json(name = "Header")
    var _headersOld: List<Header> = emptyList()

    @Json(name = "Headers")
    var headers: List<Header> = emptyList()

    @Json(name = "Rows")
    var rows2api: List<List<String>> = emptyList()

    @JsonClass(generateAdapter = true)
    class Header {

        @Json(name = "Text")
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
