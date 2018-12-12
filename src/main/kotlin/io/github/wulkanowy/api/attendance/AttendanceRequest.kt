package io.github.wulkanowy.api.attendance

import com.google.gson.annotations.SerializedName
import java.util.*

data class AttendanceRequest(

        @SerializedName("data")
        val date: Date,

        @SerializedName("idTypWpisuFrekwencji")
        val typeId: Int = -1
)
