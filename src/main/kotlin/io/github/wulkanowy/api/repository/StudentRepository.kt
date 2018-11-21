package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.grades.GradeRequest
import io.github.wulkanowy.api.grades.getGradeValueWithModifier
import io.github.wulkanowy.api.mobile.Device
import io.github.wulkanowy.api.service.StudentService
import io.reactivex.Single

class StudentRepository(private val api: StudentService) {

    fun getGrades(semesterId: Int?): Single<List<Grade>> {
        return api.getGrades(GradeRequest(semesterId)).map { res ->
            res.data?.gradesWithSubjects?.map { subject ->
                subject.grades.map {
                    val values = getGradeValueWithModifier(it.entry)
                    it.apply {
                        this.subject = subject.name
                        comment = entry.substringAfter(" (").removeSuffix(")")
                        entry = entry.substringBefore(" (")
                        if (comment == entry) comment = ""
                        value = values.first
                        date = privateDate
                        modifier = values.second
                        weight = "$weightValue,00"
                        color = if ("0" == color) "000000" else color.toInt().toString(16).toUpperCase()
                    }
                }
            }?.flatten()
        }
    }

    fun getRegisteredDevices(): Single<List<Device>> {
        return api.getRegisteredDevices().map { it.data }
    }
}
