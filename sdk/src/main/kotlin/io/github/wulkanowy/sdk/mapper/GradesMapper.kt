package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.api.grades.isGradeValid
import io.github.wulkanowy.api.toLocalDate
import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Grade
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.api.grades.Grade as ScrapperGrade
import io.github.wulkanowy.sdk.mobile.grades.Grade as ApiGrade

fun List<ApiGrade>.mapGrades(dict: Dictionaries): List<Grade> {
    return map { grade ->
        Grade(
            subject = dict.subjects.singleOrNull { it.id == grade.subjectId }?.name.orEmpty(),
            description = dict.gradeCategories.singleOrNull { it.id == grade.categoryId }?.name.orEmpty(),
            symbol = dict.gradeCategories.singleOrNull { it.id == grade.categoryId }?.code.orEmpty(),
            comment = grade.comment.orEmpty(),
            date = grade.creationDate.toLocalDate(),
            teacher = dict.teachers.singleOrNull { it.id == grade.employeeIdD }?.let { "${it.name} ${it.surname}" }.orEmpty(),
            entry = if (grade.entry.isNotBlank()) grade.entry else "...",
            weightValue = if (isGradeValid(grade.entry)) grade.gradeWeight else .0,
            modifier = grade.modificationWeight ?: .0,
            value = grade.value,
            weight = grade.weight,
            color = "0"
        )
    }
}

fun List<ScrapperGrade>.mapGrades(): List<Grade> {
    return map {
        Grade(
            subject = it.subject,
            description = it.description.orEmpty(),
            symbol = it.symbol.orEmpty(),
            comment = it.comment,
            date = it.date.toLocalDate(),
            teacher = it.teacher,
            entry = it.entry,
            weight = it.weight,
            weightValue = it.weightValue,
            color = it.color,
            value = it.value.toDouble(),
            modifier = it.modifier
        )
    }
}
