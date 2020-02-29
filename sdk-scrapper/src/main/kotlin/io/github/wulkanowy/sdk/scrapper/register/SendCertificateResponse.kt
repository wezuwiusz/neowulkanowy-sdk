package io.github.wulkanowy.sdk.scrapper.register

import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.annotation.Selector

class SendCertificateResponse {

    @Selector(".panel.linkownia.pracownik.klient a[href*=\"uonetplus-opiekun\"]")
    var oldStudentSchools: List<Element> = emptyList()

    @Selector(".panel.linkownia.pracownik.klient a[href*=\"uonetplus-uczen\"]")
    var studentSchools: List<Element> = emptyList()
}
