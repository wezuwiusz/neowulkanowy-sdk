package io.github.wulkanowy.api.homework

import io.github.wulkanowy.api.DATE_FORMAT
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class Homework {

    lateinit var date: Date

    @Format(value = DATE_FORMAT)
    @Selector(".wartosc", index = 2, regex = ".+, (.+)")
    lateinit var entryDate: Date

    @Selector(".wartosc", index = 0)
    lateinit var subject: String

    @Selector(".wartosc", index = 1)
    lateinit var content: String

    @Selector(".wartosc", index = 2, regex = "(.+), .+")
    lateinit var teacher: String
}
