package io.github.wulkanowy.sdk.scrapper.notes

enum class NoteCategory(val id: Int) {
    UNKNOWN(0),
    POSITIVE(1),
    NEUTRAL(2),
    NEGATIVE(3),
    ;

    companion object {
        fun getByValue(value: Int) = values().singleOrNull { it.id == value } ?: UNKNOWN
    }
}
