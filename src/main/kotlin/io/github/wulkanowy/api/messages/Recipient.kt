package io.github.wulkanowy.api.messages

import com.google.gson.annotations.SerializedName

data class Recipient(

        @SerializedName("Id")
        val id: String,

        @SerializedName("Nazwa")
        val name: String,

        @SerializedName("IdLogin")
        val loginId: Int,

        @SerializedName("IdJednostkaSprawozdawcza")
        val reportingUnitId: Int,

        @SerializedName("Rola")
        val role: Int
)
