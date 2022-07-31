package io.github.wulkanowy.sdk.scrapper.timetable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jsoup.nodes.Element
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class TimetableResponse(

    @SerialName("Headers")
    val headers: List<TimetableHeader> = emptyList(),

    @SerialName("Rows")
    val rows: List<List<String>> = emptyList(),

    @SerialName("Additionals")
    val additional: List<TimetableAdditionalDay>
)

@Serializable
data class TimetableHeader(

    @SerialName("Text")
    val date: String
)

@Serializable
data class TimetableAdditionalDay(

    @SerialName("Header")
    val header: String,

    @SerialName("Descriptions")
    val descriptions: List<TimetableAdditionalLesson>
)

@Serializable
data class TimetableAdditionalLesson(

    @SerialName("Description")
    val description: String
)

data class TimetableCell(
    val number: Int = 0,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val date: LocalDate,
    val td: Element
)
