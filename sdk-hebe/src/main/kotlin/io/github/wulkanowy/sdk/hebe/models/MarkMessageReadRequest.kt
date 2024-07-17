package io.github.wulkanowy.sdk.hebe.models

import kotlinx.serialization.SerialName

data class MarkMessageReadRequest(
    @SerialName("PupilId")
    val pupilId: Int? = null,
    @SerialName("BoxKey")
    val boxKey: String,
    @SerialName("MessageKey")
    val messageKey: String,
    @SerialName("Status")
    val status: Int = 1,
)
