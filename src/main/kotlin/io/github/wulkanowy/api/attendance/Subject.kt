package io.github.wulkanowy.api.attendance

import pl.droidsonroids.jspoon.annotation.Selector

data class Subject(

        @Selector("option")
        var name: String = "Wszystkie",

        @Selector("option", attr = "value")
        var value: Int = -1
)
