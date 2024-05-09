package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.CookieJarCabinet
import io.github.wulkanowy.sdk.scrapper.exception.CloudflareVerificationException
import io.github.wulkanowy.sdk.scrapper.exception.ConnectionBlockedException
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.exception.TemporarilyDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.login.AccountPermissionException
import io.github.wulkanowy.sdk.scrapper.login.InvalidSymbolException
import io.github.wulkanowy.sdk.scrapper.login.PasswordChangeRequiredException
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
                    "HTTP Error 404" -> logger.warn("ScrapperException", ScrapperException(title, HTTP_NOT_FOUND))
                    else -> logger.warn("VulcanException", VulcanException("$title. ${it.select(".errorMessage").text()}", httpCode))
                }
            }
        }

        doc.select(".app-error-container").takeIf { it.isNotEmpty() }?.let {
            if (it.select("h2").text() == "Informacja") {
                logger.warn("ServiceUnavailableException", ServiceUnavailableException(it.select("span").firstOrNull()?.text().orEmpty()))
            }
        }

        doc.select("#MainPage_ErrorDiv div").let {
            if (it.text().contains("Trwa aktualizacja bazy danych")) {
                logger.warn("ServiceUnavailableException", ServiceUnavailableException(it.last()?.ownText().orEmpty()))
            }
            if (it.last()?.ownText()?.contains("czasowo wyłączona") == true) {
                logger.warn("TemporarilyDisabledException", TemporarilyDisabledException(it.last()?.ownText().orEmpty()))
            }
            if (it.isNotEmpty()) {
                logger.warn("VulcanException", VulcanException(it[0].ownText(), httpCode))
            }
        }

        doc.select("h2.error").let {
            if (it.isNotEmpty()) logger.warn("AccountPermissionException", AccountPermissionException(it.text()))
        }
        doc.select("h2").text().let {
            if (it == "Strona nie znaleziona") logger.warn("ScrapperException", ScrapperException(it, httpCode))
        }

        doc.selectFirst("form")?.attr("action")?.let {
            if ("SetNewPassword" in it) {
                logger.debug("Set new password action url: $redirectUrl")
                throw PasswordChangeRequiredException("Wymagana zmiana hasła użytkownika", redirectUrl)
            }
        }

        doc.select("#page-error .error__box").let {
            if ("ciągiem znaków wykorzystywanym przez placówki w konkretnym mieście" in it.text()) {
                throw InvalidSymbolException(it.text())
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
            "Attention Required! | Cloudflare" -> throw ConnectionBlockedException(doc.select(".cf-error-overview").text())
            "Just a moment..." -> if (httpCode == HTTP_FORBIDDEN) throw CloudflareVerificationException(redirectUrl)

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
