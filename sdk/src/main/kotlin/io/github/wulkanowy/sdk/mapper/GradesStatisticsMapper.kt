package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.GradePointsStatistics
import io.github.wulkanowy.sdk.pojo.GradeStatisticsItem
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSemester
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSemesterSubItem
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSubject
import io.github.wulkanowy.sdk.scrapper.grades.GradePointsSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsPartial
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsSemester

internal fun List<GradesStatisticsSemester>.mapGradesSemesterStatistics() = map {
    GradeStatisticsSemester(
        subject = it.subject,
        items = it.items.orEmpty().map { item ->
            GradeStatisticsSemesterSubItem(
                grade = item.grade,
                amount = item.amount,
                isStudentHere = item.isStudentHere,
            )
        },
    )
}

internal fun List<GradesStatisticsPartial>.mapGradeStatistics() = map { partial ->
    GradeStatisticsSubject(
        subject = partial.subject,
        classAverage = partial.classSeries.average.takeIf { !partial.classSeries.isEmpty }.orEmpty(),
        classItems = partial.classSeries.items.orEmpty().map { grade ->
            GradeStatisticsItem(
                subject = partial.subject,
                grade = grade.grade,
                amount = grade.amount ?: 0,
            )
        },
        studentAverage = partial.studentSeries.average.takeIf { !partial.studentSeries.isEmpty }.orEmpty(),
        studentItems = partial.studentSeries.items.orEmpty().map { grade ->
            GradeStatisticsItem(
                subject = partial.subject,
                grade = grade.grade,
                amount = grade.amount ?: 0,
            )
        },
    )
}

internal fun List<GradePointsSummary>.mapGradePointsStatistics() = map {
    GradePointsStatistics(
        subject = it.subject,
        student = it.student,
        others = it.others,
    )
}
