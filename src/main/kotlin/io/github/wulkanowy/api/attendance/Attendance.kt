package io.github.wulkanowy.api.attendance

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.Date

class Attendance {

    @SerializedName("IdPoraLekcji")
    var number: Int = 0

    @SerializedName("Data")
    lateinit var date: Date

    @SerializedName("PrzedmiotNazwa")
    @Selector("span", defValue = "null")
    lateinit var subject: String

    @Selector("img", attr = "title", defValue = "Nieznany wpis")
    lateinit var name: String

    @Selector("div", attr = "class")
    internal lateinit var type: String // do not use

    @SerializedName("IdKategoria")
    var categoryId: Int = -1

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

    enum class Category(val id: Int, val title: String) {
        ALL(-1, "Wszystkie"),
        UNKNOWN(0, "Nieznany"),
        PRESENCE(1, "Obecność"),
        ABSENCE_UNEXCUSED(2, "Nieobecność nieusprawiedliwiona"),
        ABSENCE_EXCUSED(3, "Nieobecność usprawiedliwiona"),
        UNEXCUSED_LATENESS(4, "Spóźnienie nieusprawiedliwione"),
        EXCUSED_LATENESS(5, "Spóźnienie usprawiedliwione"),
        ABSENCE_FOR_SCHOOL_REASONS(6, "Nieobecność z przyczyn szkolnych"),
        EXEMPTION(7, "Zwolnienie"),
        DELETED(8, "Usunięty wpis")
    }
}
