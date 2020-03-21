package io.github.wulkanowy.sdk.hebe

import com.google.gson.annotations.SerializedName

class ApiResponse<T> {

    @SerializedName("Envelope")
    var envelope: T? = null

    @SerializedName("EnvelopeType")
    lateinit var envelopeType: String

    @SerializedName("InResponseTo")
    var inResponseTo: String? = null

    @SerializedName("RequestId")
    lateinit var requestId: String

    @SerializedName("Status")
    lateinit var status: Status

    @SerializedName("Timestamp")
    var timestamp: Long = 0

    @SerializedName("TimestampFormatted")
    lateinit var timestampFormatted: String

    data class Status(

        @SerializedName("Code")
        val code: Int,

        @SerializedName("Message")
        val message: String
    )
}
