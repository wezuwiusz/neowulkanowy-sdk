package io.github.wulkanowy.api.login

import io.github.wulkanowy.api.ApiException

class NotLoggedInException(message: String) : ApiException(message)
