package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.register.Semester
import io.github.wulkanowy.api.service.StudentService
import io.github.wulkanowy.api.toLocalDate
import io.reactivex.Single
import org.threeten.bp.LocalDate.now

class StudentStartRepository(
    private val studentId: Int,
    private val classId: Int,
    private val api: StudentService
) {

    fun getSemesters(): Single<List<Semester>> {
        return api.getDiaries()
            .map { it.data }
            .map { diaries ->
                diaries
                    .filter { diary -> diary.studentId == studentId }
                    .filter { diary -> diary.semesters[0].classId == classId }
            }
            .map { diaries ->
                diaries.map { diary ->
                    diary.semesters.map {
                        Semester(
                            diaryId = diary.diaryId,
                            diaryName = "${diary.level}${diary.symbol} ${diary.year}",
                            semesterId = it.id,
                            semesterNumber = it.number,
                            start = it.start.toLocalDate(),
                            end = it.end.toLocalDate(),
                            current = it.start.toLocalDate() <= now() && it.end.toLocalDate() >= now(),
                            classId = it.classId,
                            unitId = it.unitId
                        )
                    }
                }.flatten().sortedByDescending { it.semesterId }
            }.map {
                if (it.isNotEmpty() && it.singleOrNull { semester -> semester.current } == null) it.apply { first().current = true }
                else it
            }
    }
}
