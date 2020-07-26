package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.annotations.SerializedName
import java.util.Date

class Attendance {

    var number: Int = 0

    @SerializedName("IdPoraLekcji")
    var timeId: Int = 0

    @SerializedName("Data")
    lateinit var date: Date

    @SerializedName("PrzedmiotNazwa")
    lateinit var subject: String

    lateinit var name: String

    @SerializedName("IdKategoria")
    var categoryId: Int = -1

    var presence: Boolean = false

    var absence: Boolean = false

    var exemption: Boolean = false

    var lateness: Boolean = false

    var excused: Boolean = false

    var deleted: Boolean = false

    var excusable: Boolean = false

    var excuseStatus: SentExcuse.Status? = null

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
