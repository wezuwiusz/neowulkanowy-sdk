package io.github.wulkanowy.api.realized

import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.Date

class Realized {

    @Selector("h2", defValue = "01.01.1970")
    @Format("dd.MM.yyyy")
    lateinit var date: Date

    @Selector(".wartosc", index = 0, defValue = "0")
    var number: Int = 0

    @Selector(".wartosc", index = 1, regex = "^(.+?),", defValue = "")
    var subject: String = ""

    @Selector(".wartosc", index = 1, regex = "[^,]*, (.*)", defValue = "")
    var topic: String = ""

    @Selector(".wartosc", index = 2, regex = "(.+)\\s\\[.+")
    var teacher: String = ""

    @Selector(".wartosc", index = 2, regex = "\\[(.+)\\]")
    var teacherSymbol: String = ""

    @Selector(".wartosc", index = 3)
    var absence: String = ""
}
