package io.github.wulkanowy.api.school

import pl.droidsonroids.jspoon.annotation.Selector

class SchoolAndTeachersResponse {

    @Selector("table tbody tr")
    var subjects: List<Subject> = emptyList()

    class Subject {

        @Selector("td", index = 1)
        lateinit var name: String

        @Selector("td", index = 2)
        lateinit var teachers: String
    }

}
