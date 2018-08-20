package io.github.wulkanowy.api.notes

import pl.droidsonroids.jspoon.annotation.Selector

data class Note(

        @Selector(".daneWiersz:nth-child(1) .wartosc")
        val teacher: String,

        @Selector(".daneWiersz:nth-child(2) .wartosc")
        val category: String,

        @Selector(".daneWiersz:nth-child(3) .wartosc")
        val content: String
)
