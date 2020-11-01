package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.GradePointsStatistics
import io.github.wulkanowy.sdk.pojo.GradeStatisticsItem
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSubject
import io.github.wulkanowy.sdk.scrapper.grades.GradePointsSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradeStatisticsAnnualItem as ScrapperGradeStatisticsAnnualItem
import io.github.wulkanowy.sdk.scrapper.grades.GradeStatisticsSubject as ScrapperGradeStatisticsPartialItem

fun List<ScrapperGradeStatisticsAnnualItem>.mapGradesAnnualStatistics() = map {
    GradeStatisticsItem(
        subject = it.subject,
        grade = it.grade,
        amount = it.amount
    )
}

fun List<ScrapperGradeStatisticsPartialItem>.mapGradeStatistics() = map {
    GradeStatisticsSubject(
        subject = it.subject,
        average = it.average,
        items = it.items.map { grade ->
            GradeStatisticsItem(
                subject = it.subject,
                grade = grade.grade,
                amount = grade.amount
            )
        }
    )
}

fun List<GradePointsSummary>.mapGradePointsStatistics() = map {
    GradePointsStatistics(
        subject = it.subject,
        student = it.student,
        others = it.others
    )
}
