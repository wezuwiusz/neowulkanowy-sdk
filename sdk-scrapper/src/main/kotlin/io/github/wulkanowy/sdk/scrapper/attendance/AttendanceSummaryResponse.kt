package io.github.wulkanowy.sdk.scrapper.attendance

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal class AttendanceSummaryResponse {

    @SerialName("Statystyki")
    @JsonNames("statystyki")
    var items: List<Summary> = emptyList()

    @Serializable
    data class Summary(

        @SerialName("Id")
        @JsonNames("kategoriaFrekwencji")
        val id: Int,

        @SerialName("miesiace")
        val months: List<AttendanceSummaryMonth> = emptyList(),

        @SerialName("NazwaTypuFrekwencji")
        val type: String = "",

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
        @JsonNames("razem")
        val total: Int?,
    )
}

@Serializable
internal data class AttendanceSummaryMonth(
    @SerialName("miesiac")
    val month: Int,
    @SerialName("wartosc")
    val value: Int,
)
