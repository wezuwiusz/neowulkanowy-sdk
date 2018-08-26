package io.github.wulkanowy.api.interceptor

import io.github.wulkanowy.api.auth.*
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
        when(doc.title()) {
            "Błąd" -> throw VulcanException(doc.body().text())
            "Błąd strony" -> throw VulcanException(doc.select(".errorMessage").text())
            "Logowanie" -> throw AccountPermissionException(doc.select("div").last().html().split("<br>")[1].trim())
            "Dziennik UONET+" -> throw NotLoggedInException(doc.select(".loginButton").text())
            "Przerwa techniczna" -> throw ServiceUnavailableException(doc.title())
        }

        val credentialsError = doc.select(".ErrorMessage, #ErrorTextLabel")
        if (credentialsError.isNotEmpty()) {
            throw BadCredentialsException(credentialsError.text())
        }

        val snpError = doc.select("#MainPage_ErrorDiv div")
        if (snpError.isNotEmpty()) {
            throw VulcanException(snpError[0].ownText())
        }

        val snpPermError = doc.select("h2.error")
        if (snpPermError.isNotEmpty()) {
            throw AccountPermissionException(snpPermError.text())
        }
    }
}
