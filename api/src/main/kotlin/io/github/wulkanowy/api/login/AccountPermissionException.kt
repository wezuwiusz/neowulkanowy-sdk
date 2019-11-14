package io.github.wulkanowy.api.login

import io.github.wulkanowy.api.ApiException

class AccountPermissionException internal constructor(message: String) : ApiException(message)
