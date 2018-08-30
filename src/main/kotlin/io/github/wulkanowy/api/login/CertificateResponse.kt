package io.github.wulkanowy.api.login

import pl.droidsonroids.jspoon.annotation.Selector

class CertificateResponse {

    @Selector("form[name=hiddenform]", attr = "abs:action")
    lateinit var action: String

    @Selector("input[name=wa]", attr = "value")
    lateinit var wa: String

    @Selector("input[name=wresult]", attr = "value")
    lateinit var wresult: String

    @Selector("input[name=wctx]", attr = "value", defValue = "")
    var wctx: String = ""
}
