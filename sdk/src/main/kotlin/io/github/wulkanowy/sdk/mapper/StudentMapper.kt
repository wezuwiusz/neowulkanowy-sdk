package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.StudentInfo
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import io.github.wulkanowy.sdk.scrapper.student.StudentInfo as ScrapperStudentInfo

fun ScrapperStudentInfo.mapStudent(): StudentInfo {
    return StudentInfo(
        student = StudentInfo.Student(
            fullName = student.fullName,
            address = student.address,
            birthDate = student.birthDate.toLocalDate(),
            birthPlace = student.birthPlace,
            cellPhoneNumber = student.cellPhoneNumber,
            correspondenceAddress = student.correspondenceAddress,
            email = student.email,
            familyName = student.familyName,
            firstName = student.firstName,
            gender = student.gender,
            parentsNames = student.parentsNames,
            pesel = student.pesel,
            phoneNumber = student.phoneNumber,
            polishCitizenship = student.polishCitizenship,
            registeredAddress = student.registeredAddress,
            secondName = student.secondName,
            surname = student.surname
        ),
        family = family.map {
            StudentInfo.FamilyMember(
                fullName = it.fullName,
                email = it.email,
                address = it.address,
                kinship = it.kinship,
                phones = it.phones
            )
        }
    )
}
