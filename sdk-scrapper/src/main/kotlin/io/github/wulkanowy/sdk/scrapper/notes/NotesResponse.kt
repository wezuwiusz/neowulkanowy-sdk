package io.github.wulkanowy.sdk.scrapper.notes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotesResponse(

    @SerialName("Uwagi")
    val notes: List<Note> = emptyList(),
)
