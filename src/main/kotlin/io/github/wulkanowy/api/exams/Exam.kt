package io.github.wulkanowy.api.exams

import io.github.wulkanowy.api.DATE_FORMAT
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class Exam {

    lateinit var date: Date

    @Format(DATE_FORMAT)
    @Selector(".wartosc", index = 3, regex = ".+, (.+)")
    lateinit var entryDate: Date

    @Selector(".wartosc", index = 0)
    lateinit var subject: String

    @Selector(".wartosc", index = 1)
    lateinit var type: String

    @Selector(".wartosc", index = 2)
    lateinit var description: String

    @Selector(".wartosc", index = 3, regex = "(.+), .+")
    lateinit var teacher: String
}
