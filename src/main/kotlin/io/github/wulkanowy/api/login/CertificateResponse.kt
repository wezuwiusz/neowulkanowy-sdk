package io.github.wulkanowy.api.login

import pl.droidsonroids.jspoon.annotation.Selector

class CertificateResponse {

    @Selector("form[name=hiddenform]", attr = "abs:action")
    var action: String = ""

    @Selector("input[name=wa]", attr = "value")
    var wa: String = ""

    @Selector("input[name=wresult]", attr = "value")
    var wresult: String = ""

    @Selector("input[name=wctx]", attr = "value", defValue = "")
    var wctx: String = ""
}
