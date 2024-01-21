package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.CookieJarCabinet
import io.github.wulkanowy.sdk.scrapper.exception.AccountInactiveException
import io.github.wulkanowy.sdk.scrapper.exception.CloudflareVerificationException
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
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_NOT_FOUND

internal class ErrorInterceptor(
    private val cookieJarCabinet: CookieJarCabinet,
) : Interceptor {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.body?.contentType()?.subtype != "json") {
            val url = response.request.url.toString()
            checkForError(
                doc = Jsoup.parse(response.peekBody(Long.MAX_VALUE).byteStream(), null, url),
                redirectUrl = url,
                httpCode = response.code,
            )
        }

        return response
    }

    private fun checkForError(doc: Document, redirectUrl: String, httpCode: Int) {
        doc.select(".errorBlock").let {
            if (it.isNotEmpty()) {
                when (val title = it.select(".errorTitle").text()) {
                    "HTTP Error 404" -> throw ScrapperException(title, HTTP_NOT_FOUND)
                    else -> throw VulcanException("$title. ${it.select(".errorMessage").text()}", httpCode)
                }
            }
        }

        doc.select(".ErrorMessage, #ErrorTextLabel, #loginArea #errorText").text().takeIf { it.isNotBlank() }?.let {
            val errorMessage = it.trimEnd('.')
            when {
                doc.select(SELECTOR_ADFS).isNotEmpty() -> when {
                    errorMessage.isNotBlank() -> throw BadCredentialsException(errorMessage)
                    else -> logger.warn("Unexpected login page!")
                }

                else -> throw BadCredentialsException(errorMessage)
            }
        }

        doc.select(".app-error-container").takeIf { it.isNotEmpty() }?.let {
            if (it.select("h2").text() == "Informacja") {
                throw ServiceUnavailableException(it.select("span").firstOrNull()?.text().orEmpty())
            }
        }

        doc.select("#MainPage_ErrorDiv div").let {
            if (it.text().contains("Trwa aktualizacja bazy danych")) throw ServiceUnavailableException(it.last()?.ownText().orEmpty())
            if (it.last()?.ownText()?.contains("czasowo wyłączona") == true) throw TemporarilyDisabledException(it.last()?.ownText().orEmpty())
            if (it.isNotEmpty()) throw VulcanException(it[0].ownText(), httpCode)
        }

        doc.select("h2.error").let {
            if (it.isNotEmpty()) throw AccountPermissionException(it.text())
        }
        doc.select("h2").text().let {
            if (it == "Strona nie znaleziona") throw ScrapperException(it, httpCode)
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
        doc.select(".info-error-message-text").let {
            if ("Nie masz wystarczających uprawnień" in it.text()) {
                throw AccountInactiveException(it.text())
            }
        }

        when (doc.title()) {
            "Błąd" -> throw VulcanException(doc.body().text(), httpCode)
            "Błąd strony" -> throw VulcanException(doc.select(".errorMessage").text(), httpCode)
            "Logowanie" -> throw AccountPermissionException(
                buildString {
                    val newMessage = doc.select(".info-error-message-text").first()?.ownText().orEmpty()
                    val oldMessage = doc.select("div").last()?.ownText().orEmpty().split(" Jeśli")[0]
                    append(newMessage.ifBlank { oldMessage })
                },
            )

            "Login Service" -> {
                cookieJarCabinet.onLoginServiceError() // workaround for very strange (random) errors
                throw ScrapperException(doc.select("#MainDiv > div").text(), httpCode)
            }

            "Połączenie zablokowane" -> throw ConnectionBlockedException(doc.body().text())
            "Just a moment..." -> if (httpCode == HTTP_FORBIDDEN) {
                throw CloudflareVerificationException(redirectUrl)
            }

            "Przerwa" -> throw ServiceUnavailableException(doc.title())
            "Przerwa techniczna" -> throw ServiceUnavailableException(doc.title())
            "Strona nie została odnaleziona" -> throw ScrapperException(doc.title(), httpCode)
            "Strona nie znaleziona" -> throw ScrapperException(doc.selectFirst("div div")?.text().orEmpty(), httpCode)
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
