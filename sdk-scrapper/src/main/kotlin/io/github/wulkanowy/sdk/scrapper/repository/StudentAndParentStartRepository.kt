package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.exception.VulcanException
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.github.wulkanowy.sdk.scrapper.service.StudentAndParentService

class StudentAndParentStartRepository(
    private val symbol: String,
    private val schoolSymbol: String,
    private val studentId: Int,
    private val api: StudentAndParentService
) {

    suspend fun getSemesters(): List<Semester> {
        val userInfo = api.getUserInfo(studentId)
        if (!userInfo.title.startsWith("Witryna ucznia i rodzica")) throw VulcanException("Unknow page with title: ${userInfo.title}")

        return userInfo.diaries.reversed().map { diary ->
            val res = api.getDiaryInfo(diary.id, "/$symbol/$schoolSymbol/Oceny.mvc/Wszystkie")
            if (!res.title.endsWith("Oceny")) throw VulcanException("Unknow page with title: ${res.title}")
            res.semesters.map {
                Semester(diary.id,
                    diary.name,
                    diary.name.substringAfter(" ").toInt(),
                    it.semesterId,
                    it.semesterNumber,
                    "selected" == it.current && "selected" == diary.current)
            }
        }.flatten()
    }
}
