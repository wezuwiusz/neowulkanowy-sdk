package io.github.wulkanowy.api.register

import pl.droidsonroids.jspoon.annotation.Selector

class StudentAndParentResponse {

    @Selector("header[data-organization-name]", attr = "data-organization-name")
    lateinit var schoolName: String

    @Selector("#uczenDropDownList option")
    lateinit var students: List<Pupil>

    @Selector("#dziennikDropDownList option")
    lateinit var diaries: List<Diary>

    class Pupil {
        @Selector("option", attr = "value", regex = "\\=(.*)")
        var id: Int = 0

        @Selector("option")
        lateinit var name: String
    }

     class Diary {
         @Selector("option", attr = "value", regex = "\\=(.*)")
         var id: Int = 0

         @Selector("option", attr = "selected", defValue = "false")
         lateinit var selected: String

         @Selector("option")
         lateinit var name: String
     }
}
