package io.github.wulkanowy.sdk.scrapper.grades

import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector

private val validGrade = "^(\\++|-|--|=)?[0-6](\\++|-|--|=)?$".toRegex()
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
        if (this.matches(validGrade)) {
            when {
                matches("[-][0-6]|[0-6][-]".toRegex()) -> Pair(replace("-", "").toInt(), -modifierWeight)
                matches("[+][0-6]|[0-6][+]".toRegex()) -> Pair(replace("+", "").toInt(), modifierWeight)
                matches("[+]{2}[0-6]|[0-6][+]{2}".toRegex()) -> Pair(replace("++", "").toInt(), .5)
                matches("[-|=]{1,2}[0-6]|[0-6][-|=]{1,2}".toRegex()) -> Pair(replace("[-|=]{1,2}".toRegex(), "").toInt(), -.5)
                else -> Pair(this.toInt(), .0)
            }
        } else {
            Pair(0, .0)
        }
    }
}
