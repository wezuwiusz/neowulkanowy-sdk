package io.github.wulkanowy.api.register

import pl.droidsonroids.jspoon.annotation.Selector

class HomepageResponse {

    @Selector(".panel.linkownia.pracownik.klient a[href*=\"uonetplus-opiekun\"]", attr = "href")
    var oldStudentSchools: List<String> = emptyList()

    @Selector(".panel.linkownia.pracownik.klient a[href*=\"uonetplus-uczen\"]", attr = "href")
    var studentSchools: List<String> = emptyList()
}
