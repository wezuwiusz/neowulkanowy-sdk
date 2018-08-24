package io.github.wulkanowy.api.student

import io.github.wulkanowy.api.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StudentInfoTest : BaseTest() {

    private val info by lazy {
        getFixture(StudentInfoTest::class, StudentInfo::class.java, "UczenDanePodstawowe.html")
    }

    @Test fun getStudentFirstNameTest() {
        assertEquals("Maria", info.student.firstName)
    }

    @Test fun getStudentSurnameTest() {
        assertEquals("Kamińska", info.student.surname)
    }

    @Test fun getStudentSecondName() {
        assertEquals("Aneta", info.student.secondName)
    }

    @Test fun getStudentNameTest() {
        assertEquals("Maria Aneta Kamińska", info.student.fullName)
    }

    @Test fun getStudentBirthDateTest() {
        assertEquals(getDate(1970, 1, 1), info.student.birthDate)
    }

    @Test fun getStudentBirthPlaceTest() {
        assertEquals("Warszawa", info.student.birthPlace)
    }

    @Test fun getStudentPeselTest() {
        assertEquals("12345678900", info.student.pesel)
    }

    @Test fun getStudentGenderTest() {
        assertEquals("Kobieta", info.student.gender)
    }

    @Test fun isStudentPolishCitizenshipTest() {
        assertTrue(info.student.polishCitizenship == "Tak") //
    }

    @Test fun getStudentFamilyNameTest() {
        assertEquals("Nowak", info.student.familyName)
    }

    @Test fun getStudentParentsNames() {
        assertEquals("Gabriela, Kamil", info.student.parentsNames)
    }

    @Test fun getBasicAddressTest() {
        assertEquals("ul. Sportowa 16, 00-123 Warszawa", info.student.address)
    }

    @Test fun getBasicRegisteredAddressTest() {
        assertEquals("ul. Sportowa 17, 00-123 Warszawa", info.student.registeredAddress)
    }

    @Test fun getBasicCorrespondenceAddressTest() {
        assertEquals("ul. Sportowa 18, 00-123 Warszawa", info.student.correspondenceAddress)
    }

    @Test fun getContactPhoneNumberTest() {
        assertEquals("005554433", info.student.phoneNumber)
    }

    @Test fun getContactCellPhoneNumberTest() {
        assertEquals("555444333", info.student.cellPhoneNumber)
    }

    @Test fun getContactEmailTest() {
        assertEquals("wulkanowy@example.null", info.student.email)
    }

    @Test fun getFamilyMembers() {
        assertEquals(2, info.family.size)
    }

    @Test fun getNameTest() {
        assertEquals("Marianna Pająk", info.family[0].fullName)
        assertEquals("Dawid Świątek", info.family[1].fullName)
    }

    @Test fun getKinshipTest() {
        assertEquals("matka", info.family[0].kinship)
        assertEquals("ojciec", info.family[1].kinship)
    }

    @Test fun getAddressTest() {
        assertEquals("ul. Sportowa 16, 00-123 Warszawa", info.family[0].address)
        assertEquals("ul. Sportowa 18, 00-123 Warszawa", info.family[1].address)
    }

    @Test fun getTelephonesTest() {
        assertEquals("555111222", info.family[0].phones)
        assertEquals("555222111", info.family[1].phones)
    }

    @Test fun getEmailTest() {
        assertEquals("wulkanowy@example.null", info.family[0].email)
        assertEquals("wulkanowy@example.null", info.family[1].email)
    }
}
