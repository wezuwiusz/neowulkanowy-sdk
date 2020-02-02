package io.github.wulkanowy.sdk.scrapper.register

import org.jsoup.nodes.Document
import pl.droidsonroids.jspoon.annotation.Selector

class SentUnlockAccountResponse {

    @Selector("html")
    lateinit var html: Document

    @Selector("title")
    lateinit var title: String

    @Selector(".ErrorMessage, #ErrorTextLabel, .UnlockAccountSummary p, #box .box-p, #lblStatus")
    lateinit var message: String
}
