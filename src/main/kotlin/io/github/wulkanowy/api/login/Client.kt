package io.github.wulkanowy.api.login

import org.jsoup.nodes.Document
import java.net.HttpCookie

interface Client {
    fun clearCookies()
    fun getPageByUrl(url: String): Document
    fun postPageByUrl(url: String, formParams: Array<Array<String>>): Document
    fun setSymbol(symbol: String)
    fun getCookies(): MutableList<HttpCookie>
}
