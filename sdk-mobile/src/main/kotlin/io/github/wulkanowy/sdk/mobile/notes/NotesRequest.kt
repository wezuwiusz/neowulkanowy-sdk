package io.github.wulkanowy.sdk.mobile.notes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.wulkanowy.sdk.mobile.ApiRequest

@JsonClass(generateAdapter = true)
data class NotesRequest(

    @Json(name = "IdOkresKlasyfikacyjny")
    val classificationPeriodId: Int,

    @Json(name = "IdUczen")
    val studentId: Int,
) : ApiRequest()
