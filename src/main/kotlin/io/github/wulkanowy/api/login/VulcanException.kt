package io.github.wulkanowy.api.login

open class VulcanException : Exception {

    constructor(message: String) : super(message)

    constructor(message: String, e: Exception) : super(message, e)
}
