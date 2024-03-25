package io.github.wulkanowy.sdk.scrapper.register

internal fun List<Diary>.getStudentsFromDiaries(
    isParent: Boolean?,
    isEduOne: Boolean,
    unitId: Int,
): List<RegisterStudent> = filter {
        it.semesters.orEmpty().isNotEmpty() || it.kindergartenDiaryId != 0 || it.isAuthorized == false
    }
    .sortedByDescending { it.level }
    .distinctBy { listOf(it.studentId, it.semesters?.firstOrNull()?.classId ?: it.symbol) }
    .map { diary ->
        val classId = diary.semesters?.firstOrNull()?.classId ?: 0
        RegisterStudent(
            studentId = diary.studentId,
            studentName = diary.studentName.trim(),
            studentSecondName = diary.studentSecondName.orEmpty(),
            studentSurname = diary.studentSurname,
            className = diary.symbol.orEmpty(),
            classId = classId,
            isParent = isParent == true,
            isAuthorized = diary.isAuthorized == true,
            isEduOne = isEduOne,
            semesters = toSemesters(
                studentId = diary.studentId,
                classId = classId,
                unitId = unitId,
            ),
        )
    }
