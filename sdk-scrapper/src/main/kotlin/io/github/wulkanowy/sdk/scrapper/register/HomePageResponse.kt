package io.github.wulkanowy.sdk.scrapper.register

import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.annotation.Selector

internal class HomePageResponse {

    @Selector("a[href*=\"uonetplus-uczen\"]")
    var studentSchools: List<Element> = emptyList()

    @Selector(".userdata, .userinfo, .user-info")
    var userData: String = ""

    @Selector("html")
    lateinit var document: Element
}
