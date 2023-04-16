package io.github.wulkanowy.sdk.scrapper.register

import pl.droidsonroids.jspoon.annotation.Selector

internal class UnlockAccountResponse {

    @Selector(".g-recaptcha", attr = "data-sitekey")
    lateinit var recaptchaSiteKey: String
}
