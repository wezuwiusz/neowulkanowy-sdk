package io.github.wulkanowy.sdk.mobile.dictionaries

import com.google.gson.annotations.SerializedName

data class AttendanceType(

    @SerializedName("Id")
    val id: Int,

    @SerializedName("Symbol")
    val symbol: String,

    @SerializedName("Nazwa")
    val name: String,

    @SerializedName("Aktywny")
    val active: Boolean,

    @SerializedName("WpisDomyslny")
    val defaultEntry: Boolean,

    @SerializedName("IdKategoriaFrek")
    val categoryId: Int
)
