package io.github.wulkanowy.sdk.scrapper.exams

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
class ExamResponse {

    @Json(name = "SprawdzianyGroupedByDayList")
    var weeks: List<ExamDay> = emptyList()

    @JsonClass(generateAdapter = true)
    class ExamDay {

        @Json(name = "Data")
        lateinit var date: Date

        @Json(name = "Sprawdziany")
        lateinit var exams: List<Exam>
    }
}
