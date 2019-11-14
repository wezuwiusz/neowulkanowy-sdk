package io.github.wulkanowy.api.login

import io.github.wulkanowy.api.ApiException

class BadCredentialsException internal constructor(message: String) : ApiException(message)
