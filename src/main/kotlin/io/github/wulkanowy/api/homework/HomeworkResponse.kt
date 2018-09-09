package io.github.wulkanowy.api.homework

import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class HomeworkResponse {

    @Format("dd.MM.yyyy")
    @Selector(".mainContainer h2:not(:contains(Brak))", regex = "\\s(.*)")
    lateinit var date: Date

    @Selector(".mainContainer article")
    var items: List<Homework> = emptyList()
}
