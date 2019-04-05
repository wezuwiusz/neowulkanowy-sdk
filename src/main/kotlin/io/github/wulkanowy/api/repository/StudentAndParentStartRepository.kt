package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.interceptor.VulcanException
import io.github.wulkanowy.api.register.Semester
import io.github.wulkanowy.api.service.StudentAndParentService
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
                    Semester(diary.id, diary.name, diary.name.substringAfter(" ").toInt(), it.semesterId, it.semesterNumber, "selected" == it.current && "selected" == diary.current)
                }
            }
        }.toList().map { it.flatten() }
    }
}
