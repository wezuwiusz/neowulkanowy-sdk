package io.github.wulkanowy.sdk.scrapper.messages

import com.google.gson.annotations.SerializedName

data class RecipientsRequest(
    @SerializedName("paramsVo")
    val paramsVo: ParamsVo
) {
    data class ParamsVo(
        @SerializedName("IdJednostkaSprawozdawcza")
        val unitId: Int,

        @SerializedName("IdOddzial")
        val classId: Int? = null,

        @SerializedName("IdPrzedszkoleOddzial")
        val kindergartenClassId: Int? = null,

        @SerializedName("IdWychowankowieOddzial")
        val studentsClassId: Int? = null,

        @SerializedName("Poziom")
        val level: Int? = null,

        @SerializedName("Rola")
        val role: Int
    )
}
