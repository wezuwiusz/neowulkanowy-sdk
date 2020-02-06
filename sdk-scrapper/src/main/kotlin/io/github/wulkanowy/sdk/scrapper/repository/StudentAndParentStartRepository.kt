package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.interceptor.VulcanException
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.github.wulkanowy.sdk.scrapper.service.StudentAndParentService
import io.reactivex.Observable
import io.reactivex.Single

class StudentAndParentStartRepository(
    private val symbol: String,
    private val schoolSymbol: String,
    private val studentId: Int,
    private val api: StudentAndParentService
) {

    fun getSemesters(): Single<List<Semester>> {
        return api.getUserInfo(studentId).map {
            it.apply {
                if (!it.title.startsWith("Witryna ucznia i rodzica")) throw VulcanException("Unknow page with title: ${it.title}")
            }
        }.flatMapObservable { Observable.fromIterable(it.diaries.reversed()) }.flatMapSingle { diary ->
            api.getDiaryInfo(diary.id, "/$symbol/$schoolSymbol/Oceny.mvc/Wszystkie").map { res ->
                if (!res.title.endsWith("Oceny")) throw VulcanException("Unknow page with title: ${res.title}")
                res.semesters.map {
                    Semester(
                        diaryId = diary.id,
                        diaryName = diary.name,
                        schoolYear = diary.name.substringAfter(" ").toInt(),
                        semesterId = it.semesterId,
                        semesterNumber = it.semesterNumber,
                        current = "selected" == it.current && "selected" == diary.current,
                        feesEnabled = false,
                        menuEnabled = false,
                        completedLessonsEnabled = false
                    )
                }
            }
        }.toList().map { it.flatten() }
    }
}
