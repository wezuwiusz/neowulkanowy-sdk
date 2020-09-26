package io.github.wulkanowy.sdk.scrapper.attendance

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AttendanceSummaryResponse {

    @Json(name = "Podsumowanie")
    var percentage: Double = .0

    @Json(name = "Statystyki")
    var items: List<Summary> = emptyList()

    @JsonClass(generateAdapter = true)
    data class Summary(

        @Json(name = "Id")
        val id: Int,

        @Json(name = "NazwaTypuFrekwencji")
        val type: String,

        @Json(name = "Wrzesien")
        val september: Int?,

        @Json(name = "Pazdziernik")
        val october: Int?,

        @Json(name = "Listopad")
        val november: Int?,

        @Json(name = "Grudzien")
        val december: Int?,

        @Json(name = "Styczen")
        val january: Int?,

        @Json(name = "Luty")
        val february: Int?,

        @Json(name = "Marzec")
        val march: Int?,

        @Json(name = "Kwiecien")
        val april: Int?,

        @Json(name = "Maj")
        val may: Int?,

        @Json(name = "Czerwiec")
        val june: Int?,

        @Json(name = "Lipiec")
        val july: Int?,

        @Json(name = "Sierpien")
        val august: Int?,

        @Json(name = "Razem")
        val total: Int?
    )
}
