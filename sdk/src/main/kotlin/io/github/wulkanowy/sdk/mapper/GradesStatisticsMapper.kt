package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.GradePointsStatistics
import io.github.wulkanowy.sdk.pojo.GradeStatisticsItem
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSemester
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSemesterSubItem
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSubject
import io.github.wulkanowy.sdk.scrapper.grades.GradePointsSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsPartial
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsSemester

fun List<GradesStatisticsSemester>.mapGradesSemesterStatistics() = map {
    GradeStatisticsSemester(
        subject = it.subject,
        items = it.items.orEmpty().map { item ->
            GradeStatisticsSemesterSubItem(
                grade = item.grade,
                amount = item.amount,
                isStudentHere = item.isStudentHere
            )
        }
    )
}

fun List<GradesStatisticsPartial>.mapGradeStatistics() = map {
    GradeStatisticsSubject(
        subject = it.subject,
        classAverage = it.classSeries.average.orEmpty(),
        classItems = it.classSeries.items.orEmpty().map { grade ->
            GradeStatisticsItem(
                subject = it.subject,
                grade = grade.grade,
                amount = grade.amount ?: 0
            )
        },
        studentAverage = it.studentSeries.average.orEmpty(),
        studentItems = it.studentSeries.items.orEmpty().map { grade ->
            GradeStatisticsItem(
                subject = it.subject,
                grade = grade.grade,
                amount = grade.amount ?: 0
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
