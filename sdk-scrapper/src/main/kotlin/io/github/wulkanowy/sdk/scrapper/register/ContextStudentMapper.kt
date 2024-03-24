package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.getDecodedKey
import io.github.wulkanowy.sdk.scrapper.grades.GradeSemester

internal fun ContextStudent.mapToDiary(semesters: List<GradeSemester>): Pair<Boolean, Diary> {
    val context = this
    val key = getDecodedKey(context.key)
    val diarySemesters = semesters.map { semester ->
        Diary.Semester(
            number = semester.numerOkresu,
            start = semester.dataOd,
            end = semester.dataDo,
            unitId = key.unitId,
            id = semester.id,
            // todo
            isLast = false,
            level = 0,
            classId = 0,
        )
    }
    val level = context.className.takeWhile { it.isDigit() }
    return context.opiekunUcznia to Diary(
        id = context.registerId,
        studentId = key.studentId,
        studentName = context.studentName
            .substringBefore(" ", ""),
        studentSecondName = context.studentName
            .substringAfter(" ", "")
            .substringBefore(" ", ""),
        studentSurname = context.studentName
            .substringAfterLast(" ", ""),
        studentNick = "",
        isDiary = true,
        diaryId = key.diaryId,
        kindergartenDiaryId = 0,
        fosterDiaryId = 0,
        level = level.toInt(), // todo
        symbol = context.className.replace(level, ""), // todo
        name = context.className,
        year = context.registerDateFrom.year,
        semesters = diarySemesters,
        start = context.registerDateFrom,
        end = context.registerDateTo,
        componentUnitId = key.unitId,
        sioTypeId = null,
        isAdults = context.isAdults,
        isPostSecondary = context.isPolicealna,
        is13 = context.is13,
        isArtistic = context.isArtystyczna,
        isArtistic13 = context.isArtystyczna13,
        isSpecial = context.isSpecjalna,
        isKindergarten = context.isPrzedszkolak,
        isFoster = null,
        isArchived = null,
        isCharges = context.config.isPlatnosci,
        isPayments = context.config.isOplaty,
        isPayButtonOn = null,
        canMergeAccounts = context.config.isScalanieKont,
        fullName = context.studentName,
        o365PassType = null,
        isAdult = null,
        isAuthorized = !context.isAuthorizationRequired,
        citizenship = null,
    )
}
