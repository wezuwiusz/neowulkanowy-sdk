package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.exception.TemporarilyDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.login.AccountPermissionException
import io.github.wulkanowy.sdk.scrapper.login.BadCredentialsException
import io.github.wulkanowy.sdk.scrapper.login.PasswordChangeRequiredException
import okhttp3.Interceptor
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory

class ErrorInterceptor : Interceptor {

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

        doc.select(".ErrorMessage, #ErrorTextLabel, #loginArea #errorText").let {
            if (it.isNotEmpty()) throw BadCredentialsException(it.text())
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

        when (doc.title()) {
            "Błąd" -> throw VulcanException(doc.body().text())
            "Błąd strony" -> throw VulcanException(doc.select(".errorMessage").text())
            "Logowanie" -> throw AccountPermissionException(doc.select("div").last()?.ownText().orEmpty().split(" Jeśli")[0])
            "Przerwa techniczna" -> throw ServiceUnavailableException(doc.title())
            "Strona nie została odnaleziona" -> throw ScrapperException(doc.title())
            "Strona nie znaleziona" -> throw ScrapperException(doc.selectFirst("div div")?.text().orEmpty())
        }

        doc.select("h2").text().let {
            if (it == "Strona nie znaleziona") throw ScrapperException(it)
        }
    }
}
