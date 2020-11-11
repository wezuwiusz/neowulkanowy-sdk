package io.github.wulkanowy.sdk.scrapper.timetable

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.jsoup.nodes.Element
import java.util.Date

@JsonClass(generateAdapter = true)
data class TimetableResponse(

    @Json(name = "Header")
    val _headersOld: List<Header> = emptyList(),

    @Json(name = "Headers")
    val headers: List<Header> = emptyList(),

    @Json(name = "Rows")
    val rows: List<List<String>> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Header(

    @Json(name = "Text")
    val date: String
)

data class TimetableCell(
    val number: Int = 0,
    val start: Date,
    val end: Date,
    val date: Date,
    val td: Element
)
