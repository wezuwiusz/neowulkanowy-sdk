package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.annotations.SerializedName

data class AttendanceSummaryRequest(
    @SerializedName("idPrzedmiot")
    val id: Int?
)
