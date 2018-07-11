package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName
import io.github.wulkanowy.sdk.base.ApiRequest

data class DictionariesRequest(

        @SerializedName("IdUczen")
        val userId: Int,

        @SerializedName("IdOkresKlasyfikacyjny")
        val classificationPeriodId: Int,

        @SerializedName("IdOddzial")
        val classId: Int

) : ApiRequest()
