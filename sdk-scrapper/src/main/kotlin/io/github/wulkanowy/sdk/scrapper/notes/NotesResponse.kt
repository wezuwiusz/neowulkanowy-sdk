package io.github.wulkanowy.sdk.scrapper.notes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotesResponse(

    @Json(name = "Uwagi")
    val notes: List<Note> = emptyList()
)
