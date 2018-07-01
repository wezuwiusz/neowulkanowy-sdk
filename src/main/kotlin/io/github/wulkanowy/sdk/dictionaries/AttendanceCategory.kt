package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class AttendanceCategory(

    @SerializedName("Id")
    var id: Int,

    @SerializedName("Nazwa")
    var name: String,

    @SerializedName("Pozycja")
    var position: Int,

    @SerializedName("Obecnosc")
    var presence: Boolean,

    @SerializedName("Nieobecnosc")
    var absence: Boolean,

    @SerializedName("Zwolnienie")
    var exemption: Boolean,

    @SerializedName("Spoznienie")
    var lateness: Boolean,

    @SerializedName("Usprawiedliwione")
    var excused: Boolean,

    @SerializedName("Usuniete")
    var deleted: Boolean
)
