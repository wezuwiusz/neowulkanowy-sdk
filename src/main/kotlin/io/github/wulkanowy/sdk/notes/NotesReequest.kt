package io.github.wulkanowy.sdk.notes

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.base.ApiRequest

data class NotesReequest(

        @SerializedName("IdOkresKlasyfikacyjny")
        val classificationPeriodId: Int,

        @SerializedName("IdUczen")
        val studentId: Int
) : ApiRequest()
