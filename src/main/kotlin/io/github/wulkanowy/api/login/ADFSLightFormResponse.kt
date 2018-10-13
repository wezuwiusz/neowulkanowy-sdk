package io.github.wulkanowy.api.login

import pl.droidsonroids.jspoon.annotation.Selector

class ADFSLightFormResponse {

    @Selector("form", attr = "action")
    lateinit var formAction: String
}
