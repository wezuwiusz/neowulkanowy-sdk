package io.github.wulkanowy.sdk.scrapper.attendance

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.Month.SEPTEMBER
import org.threeten.bp.Month.DECEMBER
import org.threeten.bp.Month.JUNE

class AttendanceSummaryTest : BaseLocalTest() {

    private val snp by lazy {
        runBlocking { getSnpRepo(AttendanceSummaryTest::class.java, "Frekwencja.html").getAttendanceSummary(-1) }
    }

    private val snpSubjects by lazy {
        runBlocking { getSnpRepo(AttendanceSummaryTest::class.java, "Frekwencja.html").getSubjects() }
    }

    private val student by lazy {
        runBlocking { getStudentRepo(AttendanceSummaryTest::class.java, "StatystykiFrekwencji.json").getAttendanceSummary(-1) }
    }

    private val studentSubjects by lazy {
        runBlocking { getStudentRepo(AttendanceSummaryTest::class.java, "Przedmioty.json").getSubjects() }
    }

    @Test
    fun getAttendanceSummary() {
        assertEquals(10, snp.size)
        assertEquals(10, student.size)
    }

    @Test
    fun getSubjects() {
        assertEquals(17, snpSubjects.size)

        listOf(snpSubjects[0], studentSubjects[0]).map {
            it.run {
                assertEquals("Wszystkie", name)
                assertEquals(-1, value)
            }
        }
    }

    @Test
    fun getAttendance_september() {
        listOf(snp[0], student[0]).map {
            it.run {
                assertEquals(SEPTEMBER, month)
                assertEquals(32, presence)
                assertEquals(1, absence)
                assertEquals(2, absenceExcused)
                assertEquals(3, absenceForSchoolReasons)
                assertEquals(4, lateness)
                assertEquals(5, latenessExcused)
                assertEquals(6, exemption)
            }
        }
    }

    @Test
    fun getAttendance_december() {
        listOf(snp[3], student[3]).map {
            it.run {
                assertEquals(DECEMBER, month)
                assertEquals(55, presence)
                assertEquals(0, absence)
                assertEquals(33, absenceExcused)
                assertEquals(0, absenceForSchoolReasons)
                assertEquals(5, lateness)
                assertEquals(0, latenessExcused)
                assertEquals(0, exemption)
            }
        }
    }

    @Test
    fun getAttendance_august() {
        listOf(snp[9], student[9]).map {
            it.run {
                assertEquals(JUNE, month)
                assertEquals(124, presence)
                assertEquals(6, absence)
                assertEquals(2, absenceExcused)
                assertEquals(0, absenceForSchoolReasons)
                assertEquals(5, lateness)
                assertEquals(0, latenessExcused)
                assertEquals(0, exemption)
            }
        }
    }
}
