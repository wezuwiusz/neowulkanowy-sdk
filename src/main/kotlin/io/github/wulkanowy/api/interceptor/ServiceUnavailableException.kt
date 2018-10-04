package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.ApiException

class ServiceUnavailableException(message: String) : ApiException(message)
