package io.github.wulkanowy.api.notes

import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class Note {

    lateinit var date: Date

    @Selector(".wartosc", index = 0, regex = "(.+)\\s\\[.+")
    lateinit var teacher: String

    @Selector(".wartosc", index = 0, regex = "\\[(.+)\\]")
    lateinit var teacherSymbol: String

    @Selector(".wartosc", index = 1)
    lateinit var category: String

    @Selector(".wartosc", index = 2)
    lateinit var content: String
}
