package io.github.wulkanowy.api.grades

import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector

private val validGrade = "^(\\++|-|--|=)?[0-6](\\++|-|--|=)?$".toRegex()
private const val modifierWeight = 0.33f

class GradeValueConverter : ElementConverter<Int?> {

    override fun convert(node: Element, selector: Selector): Int {
        return getGradeValueWithModifier(node.text()).first
    }
}

class GradeModifierValueConverter: ElementConverter<Float> {

    override fun convert(node: Element, selector: Selector): Float {
        return getGradeValueWithModifier(node.text()).second
    }
}

private fun getGradeValueWithModifier(grade: String): Pair<Int, Float> {
    return grade.substringBefore(" (").run {
        if (this.matches(validGrade)) {
            when {
                matches("[-][0-6]|[0-6][-]".toRegex()) -> Pair(replace("-", "").toInt(), -modifierWeight)
                matches("[+][0-6]|[0-6][+]".toRegex()) -> Pair(replace("+", "").toInt(), modifierWeight)
                matches("[-|=]{1,2}[0-6]|[0-6][-|=]{1,2}".toRegex()) -> Pair(replace("[-|=]{1,2}".toRegex(), "").toInt(), -0.5f)
                else -> Pair(this.toInt(), 0f)
            }
        } else {
            Pair(0, 0f)
        }
    }
}
