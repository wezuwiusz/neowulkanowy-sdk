package io.github.wulkanowy.sdk.scrapper.register

import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.annotation.Selector

class HomePageResponse {

    @Selector(".panel.linkownia.pracownik.klient a[href*=\"uonetplus-uczen\"]")
    var studentSchools: List<Element> = emptyList()

    @Selector("html")
    lateinit var document: Element
}
