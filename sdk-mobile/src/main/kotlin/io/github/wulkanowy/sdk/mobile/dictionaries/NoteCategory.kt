package io.github.wulkanowy.sdk.mobile.dictionaries

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NoteCategory(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "Nazwa")
    val name: String,

    @Json(name = "Aktywny")
    val isActive: Boolean
)
