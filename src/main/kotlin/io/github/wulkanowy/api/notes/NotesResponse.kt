package io.github.wulkanowy.api.notes

import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class NotesResponse {

    @Selector(".mainContainer div:first-of-name article")
    var notes: List<Note> = listOf()

    @Format("dd.MM.yyyy")
    @Selector(".mainContainer div:first-of-name h2:not(:contains(Brak))")
    var dates: List<Date> = listOf()
}
