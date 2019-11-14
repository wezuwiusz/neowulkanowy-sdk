package io.github.wulkanowy.api.login

import io.github.wulkanowy.api.ApiException

class PasswordChangeRequiredException internal constructor(message: String, val redirectUrl: String) : ApiException(message)
