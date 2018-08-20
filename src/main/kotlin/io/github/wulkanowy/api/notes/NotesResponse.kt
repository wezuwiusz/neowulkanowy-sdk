package io.github.wulkanowy.api.notes

import pl.droidsonroids.jspoon.annotation.Selector

class NotesResponse {

    @Selector(".mainContainer article")
    var notes: List<Note> = listOf()

    @Selector(".mainContainer h2")
    var dates: List<String> = listOf()
}
