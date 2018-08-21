package io.github.wulkanowy.api.notes

import io.github.wulkanowy.api.DATE_FORMAT
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class NotesResponse {

    @Selector(".mainContainer div:first-of-type article")
    var notes: List<Note> = listOf()

    @Format(DATE_FORMAT)
    @Selector(".mainContainer div:first-of-type h2:not(:contains(Brak))")
    var dates: List<Date> = listOf()
}
