package io.github.wulkanowy.sdk.mobile.notes

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.mobile.ApiRequest

data class NotesRequest(

    @SerializedName("IdOkresKlasyfikacyjny")
    val classificationPeriodId: Int,

    @SerializedName("IdUczen")
    val studentId: Int
) : ApiRequest()
