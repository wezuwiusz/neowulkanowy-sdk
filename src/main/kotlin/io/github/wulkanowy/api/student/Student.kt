package io.github.wulkanowy.api.student

import io.github.wulkanowy.api.DATE_FORMAT
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class Student {

    @Selector("article:nth-of-type(1) .wartosc", index = 0)
    lateinit var fullName: String

    @Selector("article:nth-of-type(1) .wartosc", index = 0, regex = "^([^\\s]*)")
    lateinit var firstName: String

    @Selector("article:nth-of-type(1) .wartosc", index = 0, regex = "\\s([^\\s]*)\\s", defValue = "")
    lateinit var secondName: String

    @Selector("article:nth-of-type(1) .wartosc", index = 0, regex = "\\s(\\w+)\$")
    lateinit var surname: String

    @Format(DATE_FORMAT)
    @Selector("article:nth-of-type(1) .wartosc", index = 1, regex = "^(.+?),")
    lateinit var birthDate: Date

    @Selector("article:nth-of-type(1) .wartosc", index = 1, regex = "[^,]+, (.+)")
    lateinit var birthPlace: String

    @Selector("article:nth-of-type(1) .wartosc", index = 2)
    lateinit var pesel: String

    @Selector("article:nth-of-type(1) .wartosc", index = 3)
    lateinit var gender: String

    @Selector("article:nth-of-type(1) .wartosc", index = 4)
    lateinit var polishCitizenship: String

    @Selector("article:nth-of-type(1) .wartosc", index = 5)
    lateinit var familyName: String

    @Selector("article:nth-of-type(1) .wartosc", index = 6)
    lateinit var parentsNames: String

    @Selector("article:nth-of-type(2) .wartosc", index = 0)
    lateinit var address: String

    @Selector("article:nth-of-type(2) .wartosc", index = 1)
    lateinit var registeredAddress: String

    @Selector("article:nth-of-type(2) .wartosc", index = 2)
    lateinit var correspondenceAddress: String

    @Selector("article:nth-of-type(3) .wartosc", index = 0)
    lateinit var phoneNumber: String

    @Selector("article:nth-of-type(3) .wartosc", index = 1)
    lateinit var cellPhoneNumber: String

    @Selector("article:nth-of-type(3) .wartosc", index = 2)
    lateinit var email: String
}
