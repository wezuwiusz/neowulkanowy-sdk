package io.github.wulkanowy.sdk.scrapper.exams

import com.google.gson.annotations.SerializedName
import java.util.Date

class ExamResponse {

    @SerializedName("SprawdzianyGroupedByDayList")
    var weeks: List<ExamDay> = emptyList()

    class ExamDay {

        @SerializedName("Data")
        lateinit var date: Date

        @SerializedName("Sprawdziany")
        lateinit var exams: List<Exam>
    }
}
