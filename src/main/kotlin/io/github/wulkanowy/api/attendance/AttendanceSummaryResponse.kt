package io.github.wulkanowy.api.attendance

import com.google.gson.annotations.SerializedName

class AttendanceSummaryResponse {

    @SerializedName("Podsumowanie")
    var percentage: Double = .0

    @SerializedName("Statystyki")
    var items: List<Summary> = emptyList()

    data class Summary(

            @SerializedName("Id")
            val id: Int,

            @SerializedName("NazwaTypuFrekwencji")
            val type: String,

            @SerializedName("Wrzesien")
            val september: Int,

            @SerializedName("Pazdziernik")
            val october: Int,

            @SerializedName("Listopad")
            val november: Int,

            @SerializedName("Grudzien")
            val december: Int,

            @SerializedName("Styczen")
            val january: Int,

            @SerializedName("Luty")
            val february: Int,

            @SerializedName("Marzec")
            val march: Int,

            @SerializedName("Kwiecien")
            val april: Int,

            @SerializedName("Maj")
            val may: Int,

            @SerializedName("Czerwiec")
            val june: Int,

            @SerializedName("Lipiec")
            val july: Int,

            @SerializedName("Sierpien")
            val august: Int,

            @SerializedName("Razem")
            val total: Int
    )
}
