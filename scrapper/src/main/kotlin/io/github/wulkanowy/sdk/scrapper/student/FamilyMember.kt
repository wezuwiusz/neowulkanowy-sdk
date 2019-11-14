package io.github.wulkanowy.sdk.scrapper.student

import pl.droidsonroids.jspoon.annotation.Selector

class FamilyMember {

    @Selector(".wartosc", index = 0)
    lateinit var fullName: String

    @Selector(".wartosc", index = 1)
    lateinit var kinship: String

    @Selector(".wartosc", index = 2)
    lateinit var address: String

    @Selector(".wartosc", index = 3)
    lateinit var phones: String

    @Selector(".wartosc", index = 4)
    lateinit var email: String
}
