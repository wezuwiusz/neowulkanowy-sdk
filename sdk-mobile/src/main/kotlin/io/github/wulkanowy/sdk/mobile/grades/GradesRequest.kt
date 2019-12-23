package io.github.wulkanowy.sdk.mobile.grades

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.mobile.ApiRequest

data class GradesRequest(

    @SerializedName("IdOddzial")
    val classId: Int,

    @SerializedName("IdOkresKlasyfikacyjny")
    val classificationPeriodId: Int,

    @SerializedName("IdUczen")
    val studentId: Int
) : ApiRequest()
