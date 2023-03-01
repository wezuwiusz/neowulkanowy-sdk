package io.github.wulkanowy.sdk.mobile.dictionaries

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Teacher(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "Imie")
    val name: String,

    @Json(name = "Nazwisko")
    val surname: String,

    @Json(name = "Kod")
    val code: String,

    @Json(name = "Aktywny")
    val active: Boolean,

    @Json(name = "Nauczyciel")
    val teacher: Boolean,

    @Json(name = "LoginId")
    val loginId: Int,
)
