package io.github.wulkanowy.api.notes

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class NotesResponse {

    @SerializedName("Uwagi")
    @Selector(".mainContainer div:first-of-type article")
    var notes: List<Note> = listOf()

    @Format("dd.MM.yyyy")
    @Selector(".mainContainer div:first-of-type h2:not(:contains(Brak))")
    var dates: List<Date> = listOf()
}
