package io.github.wulkanowy.api.exams

import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class ExamResponse {

    @Selector(".mainContainer > div:has(h2)")
    var days: List<ExamDay> = emptyList()

    class ExamDay {

        @Format("dd.MM.yyyy")
        @Selector("h2", regex = ".+, (.+)")
        lateinit var date: Date

        @Selector("article")
        lateinit var exams: List<Exam>
    }
}
