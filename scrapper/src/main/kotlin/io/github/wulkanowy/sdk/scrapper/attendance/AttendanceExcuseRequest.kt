package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.annotations.SerializedName

data class AttendanceExcuseRequest(

    @SerializedName("usprawiedliwienie")
    val excuse: Excuse
) {

    data class Excuse(

        @SerializedName("Nieobecnosci")
        val absents: List<Absent>,

        @SerializedName("Tresc")
        val content: String?
    ) {

        data class Absent(

            @SerializedName("Data")
            val date: String,

            @SerializedName("IdPoraLekcji")
            val timeId: Int?
        )
    }
}
