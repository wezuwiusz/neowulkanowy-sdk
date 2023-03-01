package io.github.wulkanowy.sdk.mobile.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.wulkanowy.sdk.mobile.ApiRequest

@JsonClass(generateAdapter = true)
data class GradesRequest(

    @Json(name = "IdOddzial")
    val classId: Int,

    @Json(name = "IdOkresKlasyfikacyjny")
    val classificationPeriodId: Int,

    @Json(name = "IdUczen")
    val studentId: Int,
) : ApiRequest()
