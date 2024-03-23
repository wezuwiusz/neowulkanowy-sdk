package io.github.wulkanowy.sdk.scrapper.school

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class SchoolPlusTest : BaseLocalTest() {

    private val school by lazy {
        runBlocking { getStudentPlusRepo(SchoolPlusTest::class.java, "SzkolaPlus.json")
            .getSchool(1, 2, 3) }
    }

    @Test
    fun getSchool() {
        with(school) {
            Assert.assertEquals("Publiczna szkoła nr 1 im. Świętego Wulkana w Wulkanowo", name)
            Assert.assertEquals("Św. Wulkana 1, 00-000 Wulkanowo", address)
            Assert.assertEquals("123", contact)
            Assert.assertEquals("Stanisław Konarowski", headmaster)
            Assert.assertEquals("", pedagogue)
        }
    }
}
