package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class Teacher(

    @SerializedName("Id")
    val id: Int,

    @SerializedName("Imie")
    val name: String,

    @SerializedName("Nazwisko")
    val surname: String,

    @SerializedName("Kod")
    val code: String,

    @SerializedName("Aktywny")
    val active: Boolean,

    @SerializedName("Nauczyciel")
    val teacher: Boolean,

    @SerializedName("LoginId")
    val loginId: Int
)
