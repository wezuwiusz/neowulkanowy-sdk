package io.github.wulkanowy.sdk.hebe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class ApiResponse<T> {

    @SerialName("Envelope")
    var envelope: T? = null

    @SerialName("EnvelopeType")
    lateinit var envelopeType: String

    @SerialName("InResponseTo")
    var inResponseTo: String? = null

    @SerialName("RequestId")
    lateinit var requestId: String

    @SerialName("Status")
    lateinit var status: Status

    @SerialName("Timestamp")
    var timestamp: Long = 0

    @SerialName("TimestampFormatted")
    lateinit var timestampFormatted: String

    @Serializable
    data class Status(

        @SerialName("Code")
        val code: Int,

        @SerialName("Message")
        val message: String,
    )
}
