package io.github.wulkanowy.sdk.scrapper.login

import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator.Site.BASE
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator.Site.HOME
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator.Site.LOGIN
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator.Site.MESSAGES
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator.Site.STUDENT
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator.Site.STUDENT_PLUS
import java.net.URL

internal class UrlGenerator(
    private val schema: String,
    private val host: String,
    private val domainSuffix: String,
    var symbol: String,
    var schoolId: String,
) {

    constructor(url: URL, domainSuffix: String, symbol: String, schoolId: String) : this(url.protocol, url.host, domainSuffix, symbol, schoolId)

    enum class Site {
        BASE,
        LOGIN,
        HOME,
        STUDENT,
        STUDENT_PLUS,
        MESSAGES,
        ;

        val isStudent: Boolean
            get() = this == STUDENT_PLUS || this == STUDENT
    }

    fun generate(type: Site, withSchoolId: Boolean = true): String {
        if (type == BASE) return "$schema://$host"
        return "$schema://${getSubDomain(type)}$domainSuffix.$host/$symbol/${if (type.isStudent && withSchoolId) "$schoolId/" else ""}"
    }

    fun createReferer(type: Site): String {
        return when (type) {
            LOGIN -> "$schema://cufs$domainSuffix.$host/"
            STUDENT -> "$schema://uonetplus$domainSuffix.$host/"
            else -> ""
        }
    }

    private fun getSubDomain(type: Site): String {
        return when (type) {
            LOGIN -> "cufs"
            HOME -> "uonetplus"
            STUDENT -> "uonetplus-uczen"
            STUDENT_PLUS -> "uonetplus-uczenplus"
            MESSAGES -> "uonetplus-wiadomosciplus"
            else -> error("unknown")
        }
    }
}
