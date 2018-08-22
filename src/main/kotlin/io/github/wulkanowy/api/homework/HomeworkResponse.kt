package io.github.wulkanowy.api.homework

import io.github.wulkanowy.api.DATE_FORMAT
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class HomeworkResponse {

    @Format(value = DATE_FORMAT)
    @Selector(".mainContainer h2:not(:contains(Brak))", regex = "\\s(.*)")
    lateinit var date: Date

    @Selector(".mainContainer article")
    var items: List<Homework> = emptyList()
}
