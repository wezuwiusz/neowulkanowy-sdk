package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDate

data class StudentInfo(
    val student: Student,
    val family: List<FamilyMember>
) {
    data class Student(
        val fullName: String,
        val firstName: String,
        val secondName: String,
        val surname: String,
        val birthDate: LocalDate,
        val birthPlace: String,
        val pesel: String,
        val gender: String,
        val polishCitizenship: String,
        val familyName: String,
        val parentsNames: String,
        val address: String,
        val registeredAddress: String,
        val correspondenceAddress: String,
        val phoneNumber: String,
        val cellPhoneNumber: String,
        val email: String
    )

    data class FamilyMember(
        val fullName: String,
        val kinship: String,
        val address: String,
        val phones: String,
        val email: String
    )
}
