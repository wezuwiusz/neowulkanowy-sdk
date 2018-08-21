package io.github.wulkanowy.api.auth

open class VulcanException : Exception {

    constructor(message: String) : super(message)

    constructor(message: String, e: Exception) : super(message, e)
}
