package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class AttendanceCategory(

    @SerializedName("Id")
    val id: Int,

    @SerializedName("Nazwa")
    val name: String,

    @SerializedName("Pozycja")
    val position: Int,

    @SerializedName("Obecnosc")
    val presence: Boolean,

    @SerializedName("Nieobecnosc")
    val absence: Boolean,

    @SerializedName("Zwolnienie")
    val exemption: Boolean,

    @SerializedName("Spoznienie")
    val lateness: Boolean,

    @SerializedName("Usprawiedliwione")
    val excused: Boolean,

    @SerializedName("Usuniete")
    val deleted: Boolean
)
