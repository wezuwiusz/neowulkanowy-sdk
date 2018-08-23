package io.github.wulkanowy.api.login

import pl.droidsonroids.jspoon.annotation.Selector

class ADFSFormResponse {

    @Selector("#form1", attr = "action")
    lateinit var formAction: String

    @Selector("#__VIEWSTATE", attr = "value")
    lateinit var viewstate: String

    @Selector("#__VIEWSTATEGENERATOR", attr = "value")
    lateinit var viewstateGenerator: String

    @Selector("#__EVENTVALIDATION", attr = "value")
    lateinit var eventValidation: String

    @Selector("input[name=__db]", attr = "value")
    lateinit var db: String
}
