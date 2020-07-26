package io.github.wulkanowy.sdk.scrapper.grades

private val validGrade = "^(\\++|-|--|=)?[0-6](\\++|-|--|=)?$".toRegex()
private val gradeMinus = "[-][0-6]|[0-6][-]".toRegex()
private val gradePlus = "[+][0-6]|[0-6][+]".toRegex()
private val gradeDoublePlus = "[+]{2}[0-6]|[0-6][+]{2}".toRegex()
private val gradeDoubleMinus = "[-|=]{1,2}[0-6]|[0-6][-|=]{1,2}".toRegex()
private const val modifierWeight = .33

fun isGradeValid(grade: String): Boolean {
    return grade.matches(validGrade)
}

fun getGradeValueWithModifier(grade: String): Pair<Int, Double> {
    return grade.substringBefore(" (").run {
        if (matches(validGrade)) {
            when {
                matches(gradeMinus) -> replace("-", "").toInt() to -modifierWeight
                matches(gradePlus) -> replace("+", "").toInt() to modifierWeight
                matches(gradeDoublePlus) -> replace("++", "").toInt() to .5
                matches(gradeDoubleMinus) -> replace("[-|=]{1,2}".toRegex(), "").toInt() to -.5
                else -> toInt() to .0
            }
        } else 0 to .0
    }
}
