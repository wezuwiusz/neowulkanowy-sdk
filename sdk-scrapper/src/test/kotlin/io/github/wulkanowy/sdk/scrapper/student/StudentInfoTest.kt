package io.github.wulkanowy.sdk.scrapper.student

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class StudentInfoTest : BaseLocalTest() {

    private val info by lazy {
        runBlocking { getStudentRepo(StudentInfoTest::class.java, "Uczen.json").getStudentInfo() }
    }

    @Test
    fun getStudentFirstNameTest() {
        assertEquals("Maria", info.name)
    }

    @Test
    fun getStudentSurnameTest() {
        assertEquals("Kamińska", info.lastName)
    }

    @Test
    fun getStudentSecondName() {
        assertEquals("Aneta", info.middleName)
    }

    @Test
    fun getStudentNameTest() {
        assertEquals("Maria Aneta Kamińska", info.fullName)
    }

    @Test
    fun getStudentBirthDateTest() {
        assertEquals(getDate(1970, 1, 1), info.birthDate)
    }

    @Test
    fun getStudentBirthPlaceTest() {
        assertEquals("Warszawa", info.birthPlace)
    }

    @Test
    fun getStudentGenderTest() {
        assertEquals(false, info.gender)
    }

    @Test
    fun isStudentPolishCitizenshipTest() {
        assertEquals(1, info.polishCitizenship)
    }

    @Test
    fun getStudentFamilyNameTest() {
        assertEquals("Nowak", info.familyName)
    }

    @Test
    fun getStudentParentsNames() {
        assertEquals("Magdalena, Dawid", info.motherAndFatherNames)
        assertEquals("Magdalena", info.motherName)
        assertEquals("Dawid", info.fatherName)
    }

    @Test
    fun getBasicAddressTest() {
        assertEquals("ul. Sportowa 16, 00-123 Warszawa", info.address)
    }

    @Test
    fun getBasicRegisteredAddressTest() {
        assertEquals("ul. Sportowa 17, 00-123 Warszawa", info.registeredAddress)
    }

    @Test
    fun getBasicCorrespondenceAddressTest() {
        assertEquals("ul. Sportowa 18, 00-123 Warszawa", info.correspondenceAddress)
    }

    @Test
    fun getContactPhoneNumberTest() {
        assertEquals("005554433", info.homePhone)
    }

    @Test
    fun getContactCellPhoneNumberTest() {
        assertEquals("555444333", info.cellPhone)
    }

    @Test
    fun getContactEmailTest() {
        assertEquals("wulkanowy@example.null", info.email)
    }

    @Test
    fun getNameTest() {
        assertEquals("Marianna Pająk", info.guardianFirst?.fullName)
        assertEquals("Dawid Świątek", info.guardianSecond?.fullName)
    }

    @Test
    fun getKinshipTest() {
        assertEquals("matka", info.guardianFirst?.kinship)
        assertEquals("ojciec", info.guardianSecond?.kinship)
    }

    @Test
    fun getAddressTest() {
        assertEquals("ul. Sportowa 16, 00-123 Warszawa", info.guardianFirst?.address)
        assertEquals("ul. Sportowa 18, 00-123 Warszawa", info.guardianSecond?.address)
    }

    @Test
    fun getTelephonesTest() {
        assertEquals("555111222", info.guardianFirst?.cellPhone)
        assertEquals("555222111", info.guardianSecond?.cellPhone)
    }

    @Test
    fun getEmailTest() {
        assertEquals("wulkanowy@example.null", info.guardianFirst?.email)
        assertEquals("wulkanowy@example.null", info.guardianSecond?.email)
    }
}
