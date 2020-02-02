package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.exception.InvalidCaptchaException
import io.github.wulkanowy.sdk.scrapper.exception.InvalidEmailException
import io.github.wulkanowy.sdk.scrapper.exception.NoAccountFoundException
import okhttp3.Interceptor
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class PasswordResetErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        checkForError(Jsoup.parse(response.peekBody(Long.MAX_VALUE).string()))

        return response
    }

    private fun checkForError(doc: Document) {
        doc.select(".ErrorMessage")?.text()?.let { // STANDARD
            if (it.contains("Niepoprawny adres email")) throw InvalidEmailException(it)
        }

        doc.select("#lblStatus")?.text()?.let { // ADFSCards
            if (it.contains("nie zostało odnalezione lub zostało zablokowane")) throw NoAccountFoundException(it)
            if (it.contains("żądanie nie zostało poprawnie autoryzowane")) throw InvalidCaptchaException(it)
        }

        doc.select("#ErrorTextLabel")?.text()?.let { // ADFSLight
            if (it.contains("nie zostało odnalezione lub zostało zablokowane")) throw NoAccountFoundException(it)
            if (it.contains("żądanie nie zostało poprawnie autoryzowane")) throw InvalidCaptchaException(it)
        }
    }
}
