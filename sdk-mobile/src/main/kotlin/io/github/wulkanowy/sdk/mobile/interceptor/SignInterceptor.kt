package io.github.wulkanowy.sdk.mobile.interceptor

import io.github.wulkanowy.signer.signContent
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset

class SignInterceptor(
    private val privateKey: String,
    private val certKey: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()

        request.header("User-Agent", "MobileUserAgent")

        if (privateKey.isNotBlank()) {
            val signature = Buffer().run {
                original.body?.writeTo(this)
                signContent(privateKey, readString(Charset.defaultCharset()))
            }

            request.header("RequestCertificateKey", certKey)
            request.header("RequestSignatureValue", signature)
        }

        return chain.proceed(request.method(original.method, original.body).build())
    }
}
