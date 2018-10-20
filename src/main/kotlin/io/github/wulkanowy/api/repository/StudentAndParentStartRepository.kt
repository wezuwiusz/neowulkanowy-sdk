package io.github.wulkanowy.api.repository

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
        return api.getUserInfo(studentId).flatMapObservable { Observable.fromIterable(it.diaries.reversed()) }.flatMapSingle { diary ->
            api.getDiaryInfo(diary.id, "/$symbol/$schoolSymbol/Oceny.mvc/Wszystkie").map { res ->
                res.semesters.map {
                    Semester(diary.id, diary.name, it.semesterId, it.semesterNumber, "selected" == it.current && "selected" == diary.current)
                }
            }
        }.toList().map { it.flatten() }
    }

    fun getCurrentSemester(): Single<Semester> {
        return api.getSchoolInfo().flatMap { info ->
            val diary = info.diaries.first { it.current == "selected" }
            api.getGrades(0).map { semester ->
                Semester(diary.id, diary.name, semester.semesterId, semester.semesterNumber, true)
            }
        }
    }
}
