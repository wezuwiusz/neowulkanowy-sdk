package io.github.wulkanowy.api.register

import pl.droidsonroids.jspoon.annotation.Selector

class HomepageResponse {

    @Selector(".panel.linkownia.pracownik.klient a[href*=\"uonetplus-opiekun\"]", attr = "href")
    var schools: List<String> = emptyList()
}
