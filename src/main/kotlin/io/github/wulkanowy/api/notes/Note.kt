package io.github.wulkanowy.api.notes

import pl.droidsonroids.jspoon.annotation.Selector

class Note {
    lateinit var date: String

    @Selector(".wartosc", index = 0)
    lateinit var teacher: String

    @Selector(".wartosc", index = 1)
    lateinit var category: String

    @Selector(".wartosc", index = 2)
    lateinit var content: String
}
