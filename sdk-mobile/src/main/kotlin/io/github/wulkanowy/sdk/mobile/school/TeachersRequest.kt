package io.github.wulkanowy.sdk.mobile.school

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.wulkanowy.sdk.mobile.ApiRequest

@JsonClass(generateAdapter = true)
class TeachersRequest(

    @Json(name = "IdUczen")
    val studentId: Int,

    @Json(name = "IdOkresKlasyfikacyjny")
    val semesterId: Int
) : ApiRequest()
