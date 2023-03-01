package io.github.wulkanowy.sdk.scrapper.attendance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AttendanceSummaryResponse {

    @SerialName("Statystyki")
    var items: List<Summary> = emptyList()

    @Serializable
    data class Summary(

        @SerialName("Id")
        val id: Int,

        @SerialName("NazwaTypuFrekwencji")
        val type: String,

        @SerialName("Wrzesien")
        val september: Int?,

        @SerialName("Pazdziernik")
        val october: Int?,

        @SerialName("Listopad")
        val november: Int?,

        @SerialName("Grudzien")
        val december: Int?,

        @SerialName("Styczen")
        val january: Int?,

        @SerialName("Luty")
        val february: Int?,

        @SerialName("Marzec")
        val march: Int?,

        @SerialName("Kwiecien")
        val april: Int?,

        @SerialName("Maj")
        val may: Int?,

        @SerialName("Czerwiec")
        val june: Int?,

        @SerialName("Lipiec")
        val july: Int?,

        @SerialName("Sierpien")
        val august: Int?,

        @SerialName("Razem")
        val total: Int?,
    )
}
