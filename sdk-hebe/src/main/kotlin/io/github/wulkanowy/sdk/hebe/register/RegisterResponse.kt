package io.github.wulkanowy.sdk.hebe.register

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @SerializedName("LoginId")
    val loginId: Int,

    @SerializedName("RestURL")
    val restUrl: String,

    @SerializedName("UserLogin")
    val userLogin: String,

    @SerializedName("UserName")
    val userName: String
)
