package io.github.wulkanowy.api.attendance

import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class Attendance {

    var number: Int = 0

    lateinit var date: Date

    @Selector("span")
    lateinit var subject: String

    @Selector("div", attr = "class")
    lateinit var type: String

    object Types {
        const val NOT_EXIST = "x-sp-nieobecny-w-oddziale"
        const val PRESENCE = "x-obecnosc"
        const val ABSENCE_UNEXCUSED = "x-nieobecnosc-nieuspr"
        const val ABSENCE_EXCUSED = "x-nieobecnosc-uspr"
        const val ABSENCE_FOR_SCHOOL_REASONS = "x-nieobecnosc-przycz-szkol"
        const val UNEXCUSED_LATENESS = "x-sp-nieusprawiedliwione"
        const val EXCUSED_LATENESS = "x-sp-spr"
        const val EXEMPTION = "x-sp-zwolnienie"
    }
}
