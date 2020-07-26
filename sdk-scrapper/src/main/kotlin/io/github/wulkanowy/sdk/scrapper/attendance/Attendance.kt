package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.annotations.SerializedName
import java.util.Date

class Attendance {

    var number: Int = 0

    lateinit var category: Category

    var excusable: Boolean = false

    var excuseStatus: SentExcuse.Status? = null

    @SerializedName("IdPoraLekcji")
    var timeId: Int = 0

    @SerializedName("Data")
    lateinit var date: Date

    @SerializedName("PrzedmiotNazwa")
    lateinit var subject: String

    @SerializedName("IdKategoria")
    var categoryId: Int = -1

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
        DELETED(8, "Usunięty wpis");

        companion object {
            @JvmStatic
            fun getCategoryById(id: Int) = values().singleOrNull { category -> category.id == id } ?: UNKNOWN
        }
    }
}
