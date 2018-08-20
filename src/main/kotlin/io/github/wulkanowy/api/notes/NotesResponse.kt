package io.github.wulkanowy.api.notes

import io.github.wulkanowy.api.DATE_FORMAT
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class NotesResponse {

    @Selector(".mainContainer article")
    var notes: List<Note> = listOf()

    @Format(DATE_FORMAT)
    @Selector(".mainContainer h2")
    var dates: List<Date> = listOf()
}
