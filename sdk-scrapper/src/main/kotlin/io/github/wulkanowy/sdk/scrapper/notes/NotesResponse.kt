package io.github.wulkanowy.sdk.scrapper.notes

import com.google.gson.annotations.SerializedName

class NotesResponse {

    @SerializedName("Uwagi")
    var notes: List<Note> = listOf()
}
