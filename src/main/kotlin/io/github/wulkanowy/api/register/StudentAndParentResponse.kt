package io.github.wulkanowy.api.register

import pl.droidsonroids.jspoon.annotation.Selector

class StudentAndParentResponse {

    @Selector("header[data-organization-name]", attr = "data-organization-name")
    lateinit var schoolName: String

    @Selector("#uczenDropDownList option")
    lateinit var students: List<Pupil>

    @Selector("#dziennikDropDownList option")
    lateinit var diaries: List<Diary>

    @Selector("#okresyKlasyfikacyjneDropDownList option[selected]", attr = "value", defValue = "0")
    var semesterId: Int = 0

    @Selector("#okresyKlasyfikacyjneDropDownList option[selected]", defValue = "0")
    var semesterNumber: Int = 0

    class Pupil {
        @Selector("option", attr = "value", regex = "\\=(.*)")
        lateinit var id: String

        @Selector("option")
        lateinit var name: String
    }

     class Diary {
         @Selector("option", attr = "value", regex = "\\=(.*)")
         lateinit var id: String

         @Selector("option")
         lateinit var name: String
     }
}
