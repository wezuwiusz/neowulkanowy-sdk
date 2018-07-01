package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class AttendanceType(

    @SerializedName("Id")
    var id: Int,

    @SerializedName("Symbol")
    var symbol: String,

    @SerializedName("Nazwa")
    var name: String,

    @SerializedName("Aktywny")
    var active: Boolean,

    @SerializedName("WpisDomyslny")
    var defaultEntry: Boolean,

    @SerializedName("IdKategoriaFrek")
    var categoryId: Int
)
