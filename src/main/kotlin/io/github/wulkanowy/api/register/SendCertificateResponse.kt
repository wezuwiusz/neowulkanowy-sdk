package io.github.wulkanowy.api.register

import pl.droidsonroids.jspoon.annotation.Selector

class SendCertificateResponse {

    @Selector(".userdata", regex = "(.*)\\s\\(")
    var currentEmail = ""

    @Selector(".panel.linkownia.pracownik.klient a[href*=\"uonetplus-opiekun\"]", attr = "href")
    var oldStudentSchools: List<String> = emptyList()

    @Selector(".panel.linkownia.pracownik.klient a[href*=\"uonetplus-uczen\"]", attr = "href")
    var studentSchools: List<String> = emptyList()

    @Selector(".panel.wieleloginow.pracownik.klient a[href*=\"rebuild\"]", attr = "href", regex = "rebuild=(.*)")
    var emails: List<String> = emptyList()
}
