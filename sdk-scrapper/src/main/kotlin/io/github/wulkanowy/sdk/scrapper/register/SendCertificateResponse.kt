package io.github.wulkanowy.sdk.scrapper.register

import pl.droidsonroids.jspoon.annotation.Selector

class SendCertificateResponse {

    @Selector(".panel.linkownia.pracownik.klient a[href*=\"uonetplus-opiekun\"]", attr = "href")
    var oldStudentSchools: List<String> = emptyList()

    @Selector(".panel.linkownia.pracownik.klient a[href*=\"uonetplus-uczen\"]", attr = "href")
    var studentSchools: List<String> = emptyList()
}
