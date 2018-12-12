package io.github.wulkanowy.api.exams

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class Exam {

    lateinit var date: Date

    @SerializedName("DataModyfikacji")
    @Format("dd.MM.yyyy")
    @Selector(".wartosc", index = 3, regex = ".+, (.+)")
    lateinit var entryDate: Date

    @SerializedName("DisplayValue")
    @Selector(".wartosc", index = 0, regex = "^(.+)\\s.+")
    lateinit var subject: String

    @Selector(".wartosc", index = 0, regex = "\\|(.+)", defValue = "")
    lateinit var group: String

    @SerializedName("Rodzaj")
    @Selector(".wartosc", index = 1)
    lateinit var type: String

    @SerializedName("Opis")
    @Selector(".wartosc", index = 2)
    lateinit var description: String

    @SerializedName("PracownikModyfikujacyDisplay")
    @Selector(".wartosc", index = 3, regex = "(.+)\\s\\[.+")
    lateinit var teacher: String

    @Selector(".wartosc", index = 3, regex = "\\[(.+)\\]")
    lateinit var teacherSymbol: String
}
