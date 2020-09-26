package io.github.wulkanowy.sdk.scrapper.attendance

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendanceExcuseRequest(

    @Json(name = "usprawiedliwienie")
    val excuse: Excuse
) {

    @JsonClass(generateAdapter = true)
    data class Excuse(

        @Json(name = "Nieobecnosci")
        val absents: List<Absent>,

        @Json(name = "Tresc")
        val content: String?
    ) {

        @JsonClass(generateAdapter = true)
        data class Absent(

            @Json(name = "Data")
            val date: String,

            @Json(name = "IdPoraLekcji")
            val timeId: Int?
        )
    }
}
