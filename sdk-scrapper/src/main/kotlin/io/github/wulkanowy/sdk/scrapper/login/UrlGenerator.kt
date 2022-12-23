package io.github.wulkanowy.sdk.scrapper.login

import java.net.URL

class UrlGenerator(
    private val schema: String,
    private val host: String,
    var symbol: String,
    var schoolId: String,
) {

    constructor(url: URL, symbol: String, schoolId: String) : this(url.protocol, url.host, symbol, schoolId)

    enum class Site {
        BASE, LOGIN, HOME, STUDENT, MESSAGES
    }

    fun generate(type: Site): String {
        if (type == Site.BASE) return "$schema://$host"
        return "$schema://${getSubDomain(type)}.$host/$symbol/${if (type == Site.STUDENT) "$schoolId/" else ""}"
    }

    private fun getSubDomain(type: Site): String {
        return when (type) {
            Site.LOGIN -> "cufs"
            Site.HOME -> "uonetplus"
            Site.STUDENT -> "uonetplus-uczen"
            Site.MESSAGES -> "uonetplus-wiadomosciplus"
            else -> error("unknown")
        }
    }
}
