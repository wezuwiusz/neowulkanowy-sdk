package io.github.wulkanowy.api.messages

import com.google.gson.annotations.SerializedName

data class Sender(

        @SerializedName("Id")
        val id: String? = null,

        @SerializedName("Nazwa")
        val name: String? = null,

        @SerializedName("IdLogin")
        val loginId: Int? = null,

        @SerializedName("IdJednostkaSprawozdawcza")
        val reportingUnitId: Int? = null,

        @SerializedName("Rola")
        val role: Int? = null,

        @SerializedName("Hash")
        val hash: String? = null

)
