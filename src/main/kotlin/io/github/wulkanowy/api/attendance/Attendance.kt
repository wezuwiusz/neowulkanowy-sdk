package io.github.wulkanowy.api.attendance

import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class Attendance {

    var number: Int = 0

    lateinit var date: Date

    @Selector("span")
    lateinit var subject: String

    @Selector("div", attr = "class")
    lateinit var name: String

    var presence: Boolean = false

    var absence: Boolean = false

    var exemption: Boolean = false

    var lateness: Boolean = false

    var excused: Boolean = false

    var deleted: Boolean = false

    object Types {
        const val PRESENCE = "x-obecnosc"
        const val ABSENCE_UNEXCUSED = "x-nieobecnosc-nieuspr"
        const val ABSENCE_EXCUSED = "x-nieobecnosc-uspr"
        const val ABSENCE_FOR_SCHOOL_REASONS = "x-nieobecnosc-przycz-szkol"
        const val UNEXCUSED_LATENESS = "x-sp-nieusprawiedliwione"
        const val EXCUSED_LATENESS = "x-sp-spr"
        const val EXEMPTION = "x-sp-zwolnienie"
    }
}
