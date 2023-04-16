package io.github.wulkanowy.sdk.hebe.interceptor

import io.github.wulkanowy.signer.hebe.getSignatureHeaders
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset
import java.time.ZoneId
import java.time.ZonedDateTime

internal class SignInterceptor(
    private val keyId: String,
    private val privatePem: String,
    private val deviceModel: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()

        request.header("UserAgent", "Dart/2.10 (dart:io)")
        request.header("vOS", "Android")
        request.header("vDeviceModel", deviceModel)
        request.header("vAPI", "1")

        if (privatePem.isNotBlank()) {
            val signatureHeaders = Buffer().run {
                original.body?.writeTo(this)
                getSignatureHeaders(
                    keyId = keyId,
                    privatePem = privatePem,
                    body = readString(Charset.defaultCharset()),
                    requestPath = original.url.pathSegments.drop(1).joinToString("/"),
                    timestamp = ZonedDateTime.now(ZoneId.of("GMT")),
                )
            }
            signatureHeaders.forEach { (key, value) ->
                request.header(key, value)
            }
        }

        return chain.proceed(request.method(original.method, original.body).build())
    }
}
