package io.github.wulkanowy.sdk.interceptor

import io.github.wulkanowy.sdk.USER_AGENT
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.util.*

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
            val buffer = Buffer()
            original.body()?.writeTo(buffer)
            request.header("RequestCertificateKey", certificate)
            request.header("RequestSignatureValue", signContent(password,
                    buffer.readByteArray(),
                    ByteArrayInputStream(Base64.getDecoder().decode(signature)))
            )
        }

        return chain.proceed(request.method(original.method(), original.body()).build())
    }

    private fun signContent(password: String, contents: ByteArray, cert: InputStream): String {
        return Base64.getEncoder().encodeToString(Signature.getInstance("SHA1withRSA").apply {
            initSign(KeyStore.getInstance("pkcs12").apply {
                load(cert, password.toCharArray())
            }.getKey("LoginCert", password.toCharArray()) as PrivateKey)
            update(contents)
        }.sign())
    }
}
