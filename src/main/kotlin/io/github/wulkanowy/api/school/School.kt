package io.github.wulkanowy.api.school

import com.google.gson.annotations.SerializedName

data class School(

    @SerializedName("Nazwa")
    val name: String,

    @SerializedName("Adres")
    val address: String,

    @SerializedName("Kontakt")
    val contact: String,

    @SerializedName("Dyrektor")
    val headmaster: String,

    @SerializedName("Pedagog")
    val pedagogue: String
)
