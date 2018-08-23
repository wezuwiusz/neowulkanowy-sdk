package io.github.wulkanowy.api.register

import pl.droidsonroids.jspoon.annotation.Selector

class StudentAndParentResponse {

    @Selector("header[data-organization-name]", attr = "data-organization-name")
    lateinit var schoolName: String

    @Selector("#uczenDropDownList option")
    lateinit var students: List<Pupil>

    @Selector("#dziennikDropDownList option")
    lateinit var diaries: List<Diary>

    @Selector("#dziennikDropDownList option[selected]", attr = "value", regex = "\\=(.*)")
    lateinit var diaryId: String

    @Selector("#dziennikDropDownList option[selected]")
    lateinit var diaryName: String

    class Pupil {
        @Selector("option", attr = "value", regex = "\\=(.*)")
        lateinit var id: String

        @Selector("option")
        lateinit var name: String

        lateinit var schoolName: String

        var diaryId: String = ""
        var diaryName: String = ""
        var semesterId: String = ""
        var semesterNumber: String = ""
    }

     class Diary {
         @Selector("option", attr = "value", regex = "\\=(.*)")
         lateinit var id: String

         @Selector("option")
         lateinit var name: String
     }
}
