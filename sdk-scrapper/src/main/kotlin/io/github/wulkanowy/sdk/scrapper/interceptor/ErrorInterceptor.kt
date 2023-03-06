package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.exception.AccountInactiveException
import io.github.wulkanowy.sdk.scrapper.exception.ConnectionBlockedException
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.exception.TemporarilyDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.login.AccountPermissionException
import io.github.wulkanowy.sdk.scrapper.login.BadCredentialsException
import io.github.wulkanowy.sdk.scrapper.login.PasswordChangeRequiredException
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository.Companion.SELECTOR_ADFS
import okhttp3.Interceptor
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import java.net.CookieManager

class ErrorInterceptor(
    private val cookies: CookieManager,
) : Interceptor {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.body?.contentType()?.subtype != "json") {
            val url = response.request.url.toString()
            checkForError(Jsoup.parse(response.peekBody(Long.MAX_VALUE).byteStream(), null, url), url)
        }

        return response
    }

    private fun checkForError(doc: Document, redirectUrl: String) {
        doc.select(".errorBlock").let {
            if (it.isNotEmpty()) {
                throw VulcanException("${it.select(".errorTitle").text()}. ${it.select(".errorMessage").text()}")
            }
        }

        doc.select(".ErrorMessage, #ErrorTextLabel, #loginArea #errorText").takeIf { it.isNotEmpty() }?.let {
            val errorMessage = it.text().trimEnd('.')
            if (doc.select(SELECTOR_ADFS).isNotEmpty()) {
                if (errorMessage.isNotBlank()) throw BadCredentialsException(errorMessage)
                else logger.warn("Unexpected login page!")
            } else throw BadCredentialsException(errorMessage)
        }

        doc.select("#MainPage_ErrorDiv div").let {
            if (it.text().contains("Trwa aktualizacja bazy danych")) throw ServiceUnavailableException(it.last()?.ownText().orEmpty())
            if (it.last()?.ownText()?.contains("czasowo wyłączona") == true) throw TemporarilyDisabledException(it.last()?.ownText().orEmpty())
            if (it.isNotEmpty()) throw VulcanException(it[0].ownText())
        }

        doc.select("h2.error").let {
            if (it.isNotEmpty()) throw AccountPermissionException(it.text())
        }

        doc.selectFirst("form")?.attr("action")?.let {
            if ("SetNewPassword" in it) {
                logger.debug("Set new password action url: $redirectUrl")
                throw PasswordChangeRequiredException("Wymagana zmiana hasła użytkownika", redirectUrl)
            }
        }

        doc.select(".panel.wychowawstwo.pracownik.klient").let {
            if ("Brak uprawnień" in it.select(".name").text()) {
                throw AccountInactiveException(it.select(".additionalText").text())
            }
        }

        when (doc.title()) {
            "Błąd" -> throw VulcanException(doc.body().text())
            "Błąd strony" -> throw VulcanException(doc.select(".errorMessage").text())
            "Logowanie" -> throw AccountPermissionException(doc.select("div").last()?.ownText().orEmpty().split(" Jeśli")[0])
            "Login Service" -> {
                cookies.cookieStore.removeAll() // workaround for very strange (random) errors
                throw ScrapperException(doc.select("#MainDiv > div").text())
            }
            "Połączenie zablokowane" -> throw ConnectionBlockedException(doc.body().text())
            "Just a moment..." -> if (doc.select(".footer").text().contains("Cloudflare")) {
                throw ConnectionBlockedException(doc.select("#challenge-body-text").text())
            }
            "Przerwa techniczna" -> throw ServiceUnavailableException(doc.title())
            "Strona nie została odnaleziona" -> throw ScrapperException(doc.title())
            "Strona nie znaleziona" -> throw ScrapperException(doc.selectFirst("div div")?.text().orEmpty())
        }

        doc.select("h2").text().let {
            if (it == "Strona nie znaleziona") throw ScrapperException(it)
        }

        if (isBobCmn(doc, redirectUrl)) {
            throw ConnectionBlockedException("Połączenie zablokowane przez system antybotowy. Spróbuj ponownie za chwilę")
        }
    }

    private fun isBobCmn(doc: Document, redirectUrl: String): Boolean {
        if ("edu.lublin.eu" !in redirectUrl) return false
        if (doc.title().isNotBlank()) return false

        val isEmptyFavicon = doc.selectFirst("link[rel]")?.attr("href") == "data:;base64,iVBORw0KGgo="
        val isDoNotTouchTagPresent = doc.selectFirst("APM_DO_NOT_TOUCH") != null

        return isEmptyFavicon && isDoNotTouchTagPresent
    }
}
