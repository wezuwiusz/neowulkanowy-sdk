package io.github.wulkanowy.sdk.mobile.register

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CertificateResponse(

    @Json(name = "IsError")
    val isError: Boolean,

    @Json(name = "IsMessageForUser")
    val isMessageForUser: Boolean,

    @Json(name = "Message")
    val message: String?,

    @Json(name = "TokenKey")
    val tokenKey: String?,

    @Json(name = "TokenStatus")
    val tokenStatus: String?,

    @Json(name = "TokenCert")
    val tokenCert: TokenCert?,
)
