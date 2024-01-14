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
        getCookie = { uri, headers -> additionalCookieManager?.get(uri, headers) },
        putCookie = { uri, headers -> additionalCookieManager?.put(uri, headers) },
    )

    val alternativeCookieManager = MergeCookieManager(
        original = CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        },
        getCookie = { uri, headers -> additionalCookieManager?.get(uri, headers) },
        putCookie = { uri, headers -> additionalCookieManager?.put(uri, headers) },
    )

    private var additionalCookieManager: CookieManager? = null

    fun isUserCookiesExist(): Boolean {
        return userCookieManager.cookieStore.cookies.isNotEmpty()
    }

    fun onRegisterUserLogout() {
        clearUserCookieStore()
    }

    fun onUserChange() {
        clearUserCookieStore()
        clearAdditionalCookieStore()
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

    private fun clearAdditionalCookieStore() {
        // if we clear additional cookie manager, user will need to go pass through
        // cloudflare captcha more often
        additionalCookieManager?.cookieStore // ?.removeAll()
    }
}

internal class MergeCookieManager(
    private val original: CookieManager,
    private val getCookie: (URI?, Map<String, List<String>>?) -> Map<String, List<String>>?,
    private val putCookie: (URI?, Map<String, List<String>>?) -> Unit,
) : CookieManager() {

    override fun get(uri: URI?, requestHeaders: Map<String, List<String>>?): Map<String, List<String>> {
        val additionalCookie = getCookie(uri, requestHeaders)
        return merge(
            map1 = original.get(uri, requestHeaders),
            map2 = additionalCookie.orEmpty(),
        )
    }

    override fun put(uri: URI?, responseHeaders: Map<String, List<String>>?) {
        original.put(uri, responseHeaders)
        // if we add cookies to additional cookie manager we also need to clear it in some cases
        // like user change, etc
        putCookie
    }

    override fun setCookiePolicy(cookiePolicy: CookiePolicy?) {
        original.setCookiePolicy(cookiePolicy)
    }

    override fun getCookieStore(): CookieStore = original.cookieStore
}

private fun merge(map1: Map<String, List<String>>, map2: Map<String, List<String>>): Map<String, List<String>> {
    val output = mutableMapOf<String, List<String>>()
    (map1.keys + map2.keys).forEach {
        output[it] = map1[it].orEmpty() + map2[it].orEmpty()
    }
    return output
}
