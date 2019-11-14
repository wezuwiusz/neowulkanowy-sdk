package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.annotations.SerializedName
import java.util.Date

data class AttendanceRequest(

    @SerializedName("data")
    val date: Date,

    @SerializedName("idTypWpisuFrekwencji")
    val typeId: Int = -1
)
