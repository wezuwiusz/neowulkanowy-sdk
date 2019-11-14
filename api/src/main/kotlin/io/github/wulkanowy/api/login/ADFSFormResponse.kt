package io.github.wulkanowy.api.login

import pl.droidsonroids.jspoon.annotation.Selector

class ADFSFormResponse {

    @Selector("title")
    var title: String = ""

    @Selector("#form1", attr = "action")
    var formAction: String = ""

    @Selector("#__VIEWSTATE", attr = "value")
    var viewstate: String = ""

    @Selector("#__VIEWSTATEGENERATOR", attr = "value")
    var viewStateGenerator: String = ""

    @Selector("#__EVENTVALIDATION", attr = "value")
    var eventValidation: String = ""

    @Selector("input[name=__db]", attr = "value")
    var db: String = ""
}
