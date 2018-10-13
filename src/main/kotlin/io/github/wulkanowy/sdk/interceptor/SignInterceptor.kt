package io.github.wulkanowy.sdk.interceptor

import io.github.wulkanowy.sdk.USER_AGENT
import io.github.wulkanowy.signer.signContent
import okhttp3.Interceptor
import okhttp3.Response

class SignInterceptor(
        private val password: String,
        private val signature: String,
        private val certificate: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()

        request.header("User-Agent", USER_AGENT)

        if (signature.isNotBlank() && certificate.isNotBlank()) {
            request.header("RequestCertificateKey", certificate)
            request.header("RequestSignatureValue", signContent(password, signature, original.body().toString()))
        }

        return chain.proceed(request.method(original.method(), original.body()).build())
    }
}
