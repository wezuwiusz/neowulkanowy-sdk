package io.github.wulkanowy.sdk.scrapper.login

internal data class ModuleHeaders(
    val token: String,
    val appGuid: String,
    val appVersion: String,
    val symbol: String? = null,
    val email: String? = null,
    val vParamsRaw: Map<String?, String?> = emptyMap(),
    val vParamsEvaluated: Map<String, String> = emptyMap(),
)
