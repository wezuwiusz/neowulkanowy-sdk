package io.github.wulkanowy.api.realized

import com.google.gson.annotations.SerializedName

class RealizedRequest(

    @SerializedName("poczatek")
    val startDate: String,

    @SerializedName("koniec")
    val endDate: String,

    @SerializedName("idPrzedmiot")
    val subject: Int = -1
)
