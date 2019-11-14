package io.github.wulkanowy.api.student

import pl.droidsonroids.jspoon.annotation.Selector

class StudentInfo {

    @Selector(".mainContainer")
    lateinit var student: Student

    @Selector(".mainContainer > article:nth-of-type(n+4)")
    var family: List<FamilyMember> = emptyList()
}
