package io.github.wulkanowy.sdk.interceptor

import io.github.wulkanowy.sdk.USER_AGENT
import net.maciekmm.uonet.EncryptionUtils
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.ByteArrayInputStream
import java.util.*

class SignInterceptor(private var signature: String, private var certificate: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()

        request.header("User-Agent", USER_AGENT)

        if (signature.isNotBlank() && certificate.isNotBlank()) {
            val buffer = Buffer()
            original.body()?.writeTo(buffer)
            request.header("RequestCertificateKey", certificate)
            request.header("RequestSignatureValue", EncryptionUtils.signContent(
                    buffer.readByteArray(),
                    ByteArrayInputStream(Base64.getDecoder().decode(signature)))
            )
        }

        request.method(original.method(), original.body())

        return chain.proceed(request.build())
    }
}
