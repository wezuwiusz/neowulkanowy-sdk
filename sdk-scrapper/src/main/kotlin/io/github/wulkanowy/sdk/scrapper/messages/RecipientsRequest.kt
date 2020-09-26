package io.github.wulkanowy.sdk.scrapper.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipientsRequest(
    @Json(name = "paramsVo")
    val paramsVo: ParamsVo
) {

    @JsonClass(generateAdapter = true)
    data class ParamsVo(

        @Json(name = "IdJednostkaSprawozdawcza")
        val unitId: Int,

        @Json(name = "IdOddzial")
        val classId: Int? = null,

        @Json(name = "IdPrzedszkoleOddzial")
        val kindergartenClassId: Int? = null,

        @Json(name = "IdWychowankowieOddzial")
        val studentsClassId: Int? = null,

        @Json(name = "Poziom")
        val level: Int? = null,

        @Json(name = "Rola")
        val role: Int
    )
}
