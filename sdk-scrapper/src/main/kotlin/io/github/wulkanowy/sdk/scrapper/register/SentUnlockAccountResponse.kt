package io.github.wulkanowy.sdk.scrapper.register

import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.annotation.Selector

class SentUnlockAccountResponse {

    @Selector("title")
    lateinit var title: String

    @Selector(".ErrorMessage, #ErrorTextLabel, .UnlockAccountSummary p, #box .box-p")
    lateinit var message: String

    @Selector("html")
    lateinit var doc: Element
}
