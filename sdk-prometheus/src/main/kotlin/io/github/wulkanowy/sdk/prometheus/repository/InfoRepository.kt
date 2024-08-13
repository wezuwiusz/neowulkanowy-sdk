package io.github.wulkanowy.sdk.prometheus.repository

import io.github.wulkanowy.sdk.prometheus.models.AccountInfo
import io.github.wulkanowy.sdk.prometheus.service.InfoService
import org.jsoup.Jsoup

internal class InfoRepository(
    private val infoService: InfoService,
) {
    suspend fun getAccountInfo(): AccountInfo {
        val document = Jsoup.parse(infoService.getAccountInfo()).select(".user-data-row")
        var accountUUID = ""
        var login = ""
        var accountType = ""
        var name = ""
        var surname = ""
        var email = ""

        document.forEach { element ->
            element.select(".user-data-label").text().let {
                val labelContents = element.select(".user-data-personal").text()
                when (it) {
                    "Unikalny identyfikator konta" -> accountUUID = labelContents
                    "Login" -> login = labelContents
                    "Typ konta" -> accountType = labelContents
                    "Imię" -> name = labelContents
                    "Nazwisko" -> surname = labelContents
                    "Adres e-mail do odzyskiwania dostępu i powiadomień" -> email = labelContents
                }
            }
        }

        val accountInfo = AccountInfo(
            uuid = accountUUID,
            login = login,
            accountType = accountType,
            firstName = name,
            surname = surname,
            email = email,
        )

        return accountInfo
    }
}
