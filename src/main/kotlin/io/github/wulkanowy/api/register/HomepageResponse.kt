package io.github.wulkanowy.api.register

import pl.droidsonroids.jspoon.annotation.Selector

class HomepageResponse {

    @Selector(".panel.linkownia.pracownik.klient .appLink a", attr = "href")
    var schools: List<String> = emptyList()
}
