package io.github.wulkanowy.sdk.hebe.interceptor

import io.github.wulkanowy.signer.hebe.getSignatureHeaders
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.net.URLEncoder
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal class SignInterceptor(
    private val keyId: String,
    private val privatePem: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()

        request.header("user-agent", "Dart/3.3 (dart:io)")
        request.header("vos", "Android")
        request.header("vapi", "1")
        request.header("vversioncode", "549")
        request.header(
            "vdate",
            LocalDateTime.now().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.RFC_1123_DATE_TIME),
        )
        request.header(
            "vcanonicalurl",
            URLEncoder.encode(
                original.url.pathSegments
                    .drop(2)
                    .joinToString("/") +
                    original.url.query,
                "UTF-8",
            ),
        )

        if (privatePem.isNotBlank()) {
            val signatureHeaders = Buffer().run {
                original.body?.writeTo(this)
                getSignatureHeaders(
                    keyId = keyId,
                    privatePem = privatePem,
                    body = readString(Charset.defaultCharset()),
                    requestPath = original.url.pathSegments
                        .drop(1)
                        .joinToString("/"),
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
