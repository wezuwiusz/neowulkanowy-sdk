package io.github.wulkanowy.sdk.hebe.register

data class RegisterDevice(
    val loginId: Int,
    val restUrl: String,
    val userLogin: String,
    val userName: String,
    val certificateHash: String,
    val privatePem: String,
)
