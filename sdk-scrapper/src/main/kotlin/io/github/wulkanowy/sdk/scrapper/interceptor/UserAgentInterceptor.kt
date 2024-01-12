package io.github.wulkanowy.sdk.scrapper.interceptor

import io.github.wulkanowy.sdk.scrapper.defaultUserAgentTemplate
import io.github.wulkanowy.sdk.scrapper.getFormattedString
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @see <a href="https://github.com/jhy/jsoup/blob/220b77140bce70dcf9c767f8f04758b09097db14/src/main/java/org/jsoup/helper/HttpConnection.java#L59">JSoup default user agent</a>
 * @see <a href="https://developer.chrome.com/multidevice/user-agent#chrome_for_android_user_agent">User Agent Strings - Google Chrome</a>
 */
internal class UserAgentInterceptor(
    private val androidVersion: String,
    private val buildTag: String,
    private val userAgentTemplate: String,
) : Interceptor {

    private val userAgent by lazy {
        try {
            getFormattedString(
                template = userAgentTemplate.ifBlank { defaultUserAgentTemplate },
                androidVersion = androidVersion,
                buildTag = buildTag,
            )
        } catch (e: Throwable) {
            getFormattedString(
                template = defaultUserAgentTemplate,
                androidVersion = androidVersion,
                buildTag = buildTag,
            )
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val formatted = userAgent
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader("User-Agent", formatted)
                .build(),
        )
    }
}
