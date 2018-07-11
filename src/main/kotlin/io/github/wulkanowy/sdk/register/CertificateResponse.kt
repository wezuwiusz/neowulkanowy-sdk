package io.github.wulkanowy.sdk.register

import com.google.gson.annotations.SerializedName

data class CertificateResponse(

        @SerializedName("IsError")
        val isError: Boolean,

        @SerializedName("IsMessageForUser")
        val isMessageForUser: Boolean,

        @SerializedName("Message")
        val message: String?,

        @SerializedName("TokenKey")
        val tokenKey: String?,

        @SerializedName("TokenStatus")
        val tokenStatus: String,

        @SerializedName("TokenCert")
        val tokenCert: TokenCert
)
