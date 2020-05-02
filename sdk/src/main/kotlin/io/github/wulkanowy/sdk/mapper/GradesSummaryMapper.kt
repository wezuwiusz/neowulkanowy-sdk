package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.GradePointsStatistics
import io.github.wulkanowy.sdk.pojo.GradeStatistics
import io.github.wulkanowy.sdk.scrapper.grades.GradePointsSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradeStatistics as ScrapperGradeStatistics

fun List<ScrapperGradeStatistics>.mapGradeStatistics(): List<GradeStatistics> {
    return map {
        GradeStatistics(
            semesterId = it.semesterId,
            subject = it.subject,
            grade = it.grade,
            gradeValue = it.gradeValue,
            amount = it.amount ?: 0
        )
    }
}

fun List<GradePointsSummary>.mapGradePointsStatistics(): List<GradePointsStatistics> {
    return map {
        GradePointsStatistics(
            semesterId = it.semesterId,
            subject = it.subject,
            student = it.student,
            others = it.others
        )
    }
}
