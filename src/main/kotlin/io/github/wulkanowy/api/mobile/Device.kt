package io.github.wulkanowy.api.mobile

import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class Device {

    @Selector("td.cellWithButton a", attr = "href", regex = "([^\\/]+\$)")
    var id: Int = 0

    @Selector("td", index = 0)
    lateinit var name: String

    @Format(value = "dd.MM.yyyy 'godz:' HH:mm:ss")
    @Selector("td", index = 1)
    lateinit var date: Date
}
