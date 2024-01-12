package io.github.wulkanowy.sdk.scrapper

import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie
import java.net.URI

internal class CookieJarCabinet {

    val userCookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    val alternativeCookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
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
        appendUserCookiesWithAdditionalCookies()
    }

    private fun clearUserCookieStore() {
        userCookieManager.cookieStore.removeAll()
        appendUserCookiesWithAdditionalCookies()
    }

    private fun appendUserCookiesWithAdditionalCookies() {
        val additionalJar = additionalCookieManager?.cookieStore ?: return
        val cookiesWithUris = additionalJar.urIs.map {
            it to additionalJar.get(it)
        }

        cookiesWithUris.forEach { (uri, cookies) ->
            cookies.forEach {
                userCookieManager.cookieStore.add(uri, it)
            }
        }
    }
}
