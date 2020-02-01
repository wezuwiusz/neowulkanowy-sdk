package io.github.wulkanowy.sdk.scrapper.grades

import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector

private val validGrade = "^(\\++|-|--|=)?[0-6](\\++|-|--|=)?$".toRegex()
private val gradeMinus = "[-][0-6]|[0-6][-]".toRegex()
private val gradePlus = "[+][0-6]|[0-6][+]".toRegex()
private val gradeDoublePlus = "[+]{2}[0-6]|[0-6][+]{2}".toRegex()
private val gradeDoubleMinus = "[-|=]{1,2}[0-6]|[0-6][-|=]{1,2}".toRegex()
private const val modifierWeight = .33

class GradeValueConverter : ElementConverter<Int?> {

    override fun convert(node: Element, selector: Selector): Int {
        return getGradeValueWithModifier(node.text()).first
    }
}

class GradeModifierValueConverter : ElementConverter<Double> {

    override fun convert(node: Element, selector: Selector): Double {
        return getGradeValueWithModifier(node.text()).second
    }
}

class GradeWeightValueConverter : ElementConverter<Double> {

    override fun convert(node: Element, selector: Selector): Double {
        return if (isGradeValid(node.parent().select("td")[1].text().substringBefore(" ("))) {
            node.text().replace(",", ".").toDouble()
        } else .0
    }
}

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
