package io.github.wulkanowy.api.attendance

import com.google.gson.annotations.SerializedName

data class AttendanceSummaryRequest(
        @SerializedName("idPrzedmiot")
        val id: Int?
)
