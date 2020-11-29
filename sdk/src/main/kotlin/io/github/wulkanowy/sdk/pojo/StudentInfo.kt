package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate

data class StudentInfo(
    val fullName: String,
    val firstName: String,
    val secondName: String,
    val surname: String,
    val birthDate: LocalDate,
    val birthPlace: String,
    val gender: StudentGender,
    val hasPolishCitizenship: Boolean,
    val familyName: String,
    val parentsNames: String,
    val address: String,
    val registeredAddress: String,
    val correspondenceAddress: String,
    val phoneNumber: String,
    val cellPhoneNumber: String,
    val email: String,
    val guardians: List<StudentGuardian>
)

data class StudentGuardian(
    val fullName: String,
    val kinship: String,
    val address: String,
    val phones: String,
    val email: String
)

enum class StudentGender {
    MALE,
    FEMALE
}
