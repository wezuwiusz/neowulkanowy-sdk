package io.github.wulkanowy.sdk.scrapper.register

import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.annotation.Selector

internal class HomePageResponse {

    @Selector(".panel.linkownia.pracownik.klient a[href*=\"uonetplus-uczen\"]")
    var studentSchools: List<Element> = emptyList()

    @Selector(".userdata")
    var userData: String = ""

    @Selector("html")
    lateinit var document: Element
}
