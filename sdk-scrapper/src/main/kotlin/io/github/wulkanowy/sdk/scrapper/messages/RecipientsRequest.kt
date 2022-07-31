package io.github.wulkanowy.sdk.scrapper.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipientsRequest(
    @SerialName("paramsVo")
    val paramsVo: ParamsVo
) {
    @Serializable
    data class ParamsVo(

        @SerialName("IdJednostkaSprawozdawcza")
        val unitId: Int,

        @SerialName("IdOddzial")
        val classId: Int? = null,

        @SerialName("IdPrzedszkoleOddzial")
        val kindergartenClassId: Int? = null,

        @SerialName("IdWychowankowieOddzial")
        val studentsClassId: Int? = null,

        @SerialName("Poziom")
        val level: Int? = null,

        @SerialName("Rola")
        val role: Int
    )
}
