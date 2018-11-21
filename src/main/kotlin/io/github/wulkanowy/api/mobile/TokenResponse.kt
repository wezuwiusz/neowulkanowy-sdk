package io.github.wulkanowy.api.mobile

import pl.droidsonroids.jspoon.annotation.Selector

class TokenResponse {

    @Selector("#rejestracja-formularz .blockElement:nth-last-child(3)", regex = ": (.*)")
    lateinit var token: String

    @Selector("#rejestracja-formularz .blockElement:nth-last-child(2)", regex = ": (.*)")
    lateinit var symbol: String

    @Selector("#rejestracja-formularz .blockElement:nth-last-child(1)", regex = ": (.*)")
    lateinit var pin: String
}
