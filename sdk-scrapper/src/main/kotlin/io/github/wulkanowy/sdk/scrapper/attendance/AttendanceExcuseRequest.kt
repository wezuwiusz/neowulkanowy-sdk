package io.github.wulkanowy.sdk.scrapper.attendance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AttendanceExcuseRequest(

    @SerialName("usprawiedliwienie")
    val excuse: Excuse,
) {

    @Serializable
    data class Excuse(

        @SerialName("Nieobecnosci")
        val absents: List<Absent>,

        @SerialName("Tresc")
        val content: String?,
    ) {

        @Serializable
        data class Absent(

            @SerialName("Data")
            val date: String,

            @SerialName("IdPoraLekcji")
            val timeId: Int? = null,
        )
    }
}
