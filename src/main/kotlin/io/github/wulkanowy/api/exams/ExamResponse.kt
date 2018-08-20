package io.github.wulkanowy.api.exams

import io.github.wulkanowy.api.DATE_FORMAT
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class ExamResponse {

    @Selector(".mainContainer > div:has(h2)")
    lateinit var days: List<ExamDay>

    class ExamDay {

        @Format(DATE_FORMAT)
        @Selector("h2", regex = ".+, (.+)")
        lateinit var date: Date

        @Selector("article")
        lateinit var exams: List<Exam>
    }
}
