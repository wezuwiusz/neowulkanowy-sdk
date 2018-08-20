package io.github.wulkanowy.api.notes

import pl.droidsonroids.jspoon.annotation.Selector

data class Note(

        var date: String = "",

        @Selector(".wartosc", index = 0)
        var teacher: String = "",

        @Selector(".wartosc", index = 1)
        var category: String = "",

        @Selector(".wartosc", index = 2)
        var content: String = ""
)
