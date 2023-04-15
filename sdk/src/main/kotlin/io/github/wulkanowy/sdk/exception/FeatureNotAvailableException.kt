package io.github.wulkanowy.sdk.exception

import java.io.IOException

class FeatureNotAvailableException internal constructor(message: String) : IOException(message)
