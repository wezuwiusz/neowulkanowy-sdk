package io.github.wulkanowy.sdk.scrapper.register

import pl.droidsonroids.jspoon.annotation.Selector

class UnlockAccount {

    @Selector(".g-recaptcha", attr = "data-sitekey")
    lateinit var recaptcha: String
}
