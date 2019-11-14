package io.github.wulkanowy.sdk.mobile.messages

import com.google.gson.annotations.SerializedName

data class Recipient(

    @SerializedName("LoginId")
    val loginId: Int,

    @SerializedName("Nazwa")
    val name: String
)
