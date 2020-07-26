package io.github.wulkanowy.sdk.scrapper.notes

import com.google.gson.annotations.SerializedName

data class NotesResponse(

    @SerializedName("Uwagi")
    val notes: List<Note> = emptyList()
)
