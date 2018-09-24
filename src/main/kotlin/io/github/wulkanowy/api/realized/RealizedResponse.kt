package io.github.wulkanowy.api.realized

import pl.droidsonroids.jspoon.annotation.Selector

class RealizedResponse {

    @Selector(".mainContainer h2, .mainContainer article")
    var items: List<Realized> = emptyList()
}
