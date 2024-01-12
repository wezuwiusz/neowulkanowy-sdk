package io.github.wulkanowy.sdk.scrapper

import java.net.CookieManager
import java.net.CookiePolicy
import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI

internal class CookieJarCabinet {

    val userCookieManager = MergeCookieManager(
        original = CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        },
    ) { uri, headers ->
        additionalCookieManager?.get(uri, headers)
    }

    val alternativeCookieManager = MergeCookieManager(
        CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        },
    ) { uri, headers ->
        additionalCookieManager?.get(uri, headers)
    }

    private var additionalCookieManager: CookieManager? = null

    fun isUserCookiesExist(): Boolean {
        return userCookieManager.cookieStore.cookies.isNotEmpty()
    }

    fun onUserChange() {
        clearUserCookieStore()
    }

    fun beforeUserLogIn() {
        clearUserCookieStore()
    }

    fun onLoginServiceError() {
        clearUserCookieStore()
    }

    fun addStudentCookie(uri: URI, cookie: HttpCookie) {
        userCookieManager.cookieStore.add(uri, cookie)
    }

    fun setAdditionalCookieManager(cookieManager: CookieManager) {
        additionalCookieManager = cookieManager
    }

    private fun clearUserCookieStore() {
        userCookieManager.cookieStore.removeAll()
    }
}

internal class MergeCookieManager(
    private val original: CookieManager,
    private val getCookie: (URI?, Map<String, List<String>>?) -> Map<String, List<String>>?,
) : CookieManager() {

    override fun get(uri: URI?, requestHeaders: Map<String, List<String>>?): Map<String, List<String>> {
        val additionalCookie = getCookie(uri, requestHeaders)
        return original.get(uri, requestHeaders) + additionalCookie.orEmpty()
    }

    override fun put(uri: URI?, responseHeaders: Map<String, List<String>>?) {
        original.put(uri, responseHeaders)
    }

    override fun setCookiePolicy(cookiePolicy: CookiePolicy?) {
        original.setCookiePolicy(cookiePolicy)
    }

    override fun getCookieStore(): CookieStore {
        return original.cookieStore
    }
}
