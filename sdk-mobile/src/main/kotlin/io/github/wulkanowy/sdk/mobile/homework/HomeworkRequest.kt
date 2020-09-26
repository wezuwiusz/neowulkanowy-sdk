package io.github.wulkanowy.sdk.mobile.homework

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.wulkanowy.sdk.mobile.ApiRequest

@JsonClass(generateAdapter = true)
data class HomeworkRequest(

    @Json(name = "DataPoczatkowa")
    val startDate: String,

    @Json(name = "DataKoncowa")
    val endDate: String,

    @Json(name = "IdOddzial")
    val classId: Int,

    @Json(name = "IdOkresKlasyfikacyjny")
    val classificationPeriodId: Int,

    @Json(name = "IdUczen")
    val studentId: Int
) : ApiRequest()
