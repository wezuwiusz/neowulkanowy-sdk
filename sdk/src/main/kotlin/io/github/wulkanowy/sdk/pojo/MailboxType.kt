package io.github.wulkanowy.sdk.pojo

enum class MailboxType(val letter: String) {

    STUDENT("U"),
    PARENT("R"),
    GUARDIAN("O"),
    EMPLOYEE("P"),

    UNKNOWN(""),
    ;

    companion object {
        fun fromLetter(letter: String) = values().find { it.letter == letter } ?: UNKNOWN
    }
}
