package io.github.wulkanowy.sdk.mobile.attendance

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.mobile.attendance.Attendance

data class AttendanceResponse(

    @SerializedName("DataPoczatkowa")
    val dateStart: Long,

    @SerializedName("DataPoczatkowaTekst")
    val dateStartText: String,

    @SerializedName("DataKoncowa")
    val dateEnd: Long,

    @SerializedName("DataKoncowaTekst")
    val dateEndText: String,

    @SerializedName("Frekwencje")
    val data: List<Attendance>
)
