package io.github.wulkanowy.sdk.scrapper.register

import pl.droidsonroids.jspoon.annotation.Selector

class StudentAndParentResponse {

    @Selector("title")
    lateinit var title: String

    @Selector("header[data-organization-name]", attr = "data-organization-name")
    lateinit var schoolName: String

    @Selector("#uczenDropDownList option")
    lateinit var students: List<Student>

    @Selector("#dziennikDropDownList option")
    lateinit var diaries: List<Diary>

    @Selector("#okresyKlasyfikacyjneDropDownList option")
    lateinit var semesters: List<Semester>

    class Student {
        @Selector("option", attr = "value", regex = "\\=(.*)")
        var id: Int = 0

        @Selector("option")
        lateinit var name: String

        lateinit var description: String

        lateinit var className: String

        var classId: Int = 0

        var isParent = false
    }

    class Diary {
        @Selector("option", attr = "value", regex = "\\=(.*)")
        var id: Int = 0

        @Selector("option")
        lateinit var name: String

        @Selector("option", attr = "selected")
        lateinit var current: String
    }

    class Semester {

        @Selector("option", attr = "value")
        var semesterId: Int = 0

        @Selector("option")
        var semesterNumber: Int = 0

        @Selector("option", attr = "selected")
        lateinit var current: String
    }
}
