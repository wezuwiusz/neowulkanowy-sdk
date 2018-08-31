package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.register.Semester
import io.github.wulkanowy.api.service.StudentAndParentService
import io.reactivex.Observable
import io.reactivex.Single

class StudentAndParentStartRepository(
        private val symbol: String,
        private val schoolId: String,
        private val studentId: String,
        private val api: StudentAndParentService
) {

    fun getSemesters(): Single<List<Semester>> {
        return api.getUserInfo(studentId).flatMapObservable { Observable.fromIterable(it.diaries.reversed()) }.flatMapSingle { diary ->
            api.getDiaryInfo(diary.id, "/$symbol/$schoolId/Oceny.mvc/Wszystkie").map { res ->
                listOf(1, 2).map { it ->
                    Semester(diary.id, diary.name, if (it == res.semesterNumber) res.semesterId else {
                        if (it < res.semesterNumber) res.semesterId - 1 else res.semesterId + 1
                    }, it)
                }
            }
        }.toList().map { it.flatten() }
    }
}
