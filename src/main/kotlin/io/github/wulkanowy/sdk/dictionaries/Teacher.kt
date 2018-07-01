package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class Teacher(

    @SerializedName("Id")
    var id: Int,

    @SerializedName("Imie")
    var name: String,

    @SerializedName("Nazwisko")
    var surname: String,

    @SerializedName("Kod")
    var code: String,

    @SerializedName("Aktywny")
    var active: Boolean,

    @SerializedName("Nauczyciel")
    var teacher: Boolean,

    @SerializedName("LoginId")
    var loginId: Int
)
