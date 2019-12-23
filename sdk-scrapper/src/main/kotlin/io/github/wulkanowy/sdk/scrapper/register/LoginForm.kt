package io.github.wulkanowy.sdk.scrapper.register

import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.annotation.Selector

class LoginForm {

    @Selector("html")
    lateinit var page: Element
}
