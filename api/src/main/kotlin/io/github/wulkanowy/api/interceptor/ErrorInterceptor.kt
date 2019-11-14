package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.login.AccountPermissionException
import io.github.wulkanowy.api.login.BadCredentialsException
import io.github.wulkanowy.api.login.PasswordChangeRequiredException
import okhttp3.Interceptor
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory

class ErrorInterceptor : Interceptor {

    companion object {
        @JvmStatic private val logger = LoggerFactory.getLogger(this::class.java)
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        checkForError(Jsoup.parse(response.peekBody(Long.MAX_VALUE).string()), response.request().url().toString())

        return response
    }

    private fun checkForError(doc: Document, redirectUrl: String) {
        doc.select(".errorBlock").let {
            if (it.isNotEmpty()) throw VulcanException("${it.select(".errorTitle").text()}. ${it.select(".errorMessage").text()}")
        }

        doc.select(".ErrorMessage, #ErrorTextLabel").let {
            if (it.isNotEmpty()) throw BadCredentialsException(it.text())
        }

        doc.select("#MainPage_ErrorDiv div").let {
            if (it?.last()?.ownText()?.startsWith("Trwa aktualizacja bazy danych") == true) throw ServiceUnavailableException(it.last().ownText())
            if (it.isNotEmpty()) throw VulcanException(it[0].ownText())
        }

        doc.select("h2.error").let {
            if (it.isNotEmpty()) throw AccountPermissionException(it.text())
        }

        when (doc.title()) {
            "Błąd" -> throw VulcanException(doc.body().text())
            "Błąd strony" -> throw VulcanException(doc.select(".errorMessage").text())
            "Logowanie" -> throw AccountPermissionException(doc.select("div").last().ownText().split(" Jeśli")[0])
            "Przerwa techniczna" -> throw ServiceUnavailableException(doc.title())
            "Strona nie została odnaleziona" -> throw ApiException(doc.title())
            "Strona nie znaleziona" -> throw ApiException(doc.selectFirst("div div").text())
            "Zmiana hasła użytkownika" -> { logger.debug(redirectUrl); throw PasswordChangeRequiredException("Wymagana zmiana hasła użytkownika", redirectUrl) }
        }

        doc.select("h2").text().let {
            if (it == "Strona nie znaleziona") throw ApiException(it)
        }
    }
}
