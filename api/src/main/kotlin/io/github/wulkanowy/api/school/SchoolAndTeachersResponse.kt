package io.github.wulkanowy.api.school

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Selector

class SchoolAndTeachersResponse {

    @SerializedName("Nauczyciele")
    var teachers: List<Teacher> = emptyList()

    @SerializedName("Szkola")
    lateinit var school: School

    @Selector("table tbody tr")
    var subjects: List<Subject> = emptyList()

    class Subject {

        @Selector("td", index = 1)
        lateinit var name: String

        @Selector("td", index = 2)
        lateinit var teachers: String
    }

    @Selector(".wartosc", index = 0)
    var name: String = ""

    @Selector(".wartosc", index = 1)
    var address: String = ""

    @Selector(".wartosc", index = 2)
    var contact: String = ""

    @Selector(".wartosc", index = 3)
    var headmaster: String = ""

    @Selector(".wartosc", index = 4)
    var pedagogue: String = ""
}
