package io.github.wulkanowy.api.grades

import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class Grade {

    @Selector("td", index = 0)
    lateinit var subject: String

    @Selector("td", index = 1)
    lateinit var value: String

    @Selector("td .ocenaCzastkowa", attr = "style", regex = "#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})")
    lateinit var color: String

    @Selector("td", index = 2, regex = "^(.+?),")
    lateinit var symbol: String

    @Selector("td", index = 2, regex = "[^,]+, (.+)")
    lateinit var description: String

    @Selector("td", index = 3)
    lateinit var weight: String

    @Selector("td:not(:empty)", index = 4, defValue = "01.01.1970")
    @Format("dd.MM.yyyy")
    lateinit var date: Date

    @Selector("td", index = 5)
    lateinit var teacher: String
}
