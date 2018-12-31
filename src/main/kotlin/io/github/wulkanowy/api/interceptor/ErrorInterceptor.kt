package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.login.AccountPermissionException
import io.github.wulkanowy.api.login.BadCredentialsException
import okhttp3.Interceptor
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        checkForError(Jsoup.parse(response.peekBody(Long.MAX_VALUE).string()))

        return response
    }

    private fun checkForError(doc: Document) {
        doc.select(".errorBlock").let {
            if (it.isNotEmpty()) throw VulcanException("${it.select(".errorTitle").text()}. ${it.select(".errorMessage").text()}")
        }

        doc.select(".ErrorMessage, #ErrorTextLabel").let {
            if (it.isNotEmpty()) throw BadCredentialsException(it.text())
        }

        doc.select("#MainPage_ErrorDiv div").let {
            if (it.isNotEmpty()) throw VulcanException(it[0].ownText())
        }

        doc.select("h2.error").let {
            if (it.isNotEmpty()) throw AccountPermissionException(it.text())
        }

        when (doc.title()) {
            "Błąd" -> throw VulcanException(doc.body().text())
            "Błąd strony" -> throw VulcanException(doc.select(".errorMessage").text())
            "Logowanie" -> throw AccountPermissionException(doc.select("div").last().html().split("<br>")[1].trim())
            "Przerwa techniczna" -> throw ServiceUnavailableException(doc.title())
            "Strona nie została odnaleziona" -> throw ApiException(doc.title())
        }

        doc.select("h2").text().let {
            if (it == "Strona nie znaleziona") throw ApiException(it)
        }
    }
}
