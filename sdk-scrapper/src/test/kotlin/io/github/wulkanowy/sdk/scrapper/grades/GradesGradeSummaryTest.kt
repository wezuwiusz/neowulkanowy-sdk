package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesGradeSummaryTest : BaseLocalTest() {

    private val std by lazy {
        getSnpRepo(GradesTest::class.java, "OcenyWszystkie-subjects.html").getGradesSummary(0).blockingGet()
    }

    private val snp by lazy {
        getSnpRepo(GradesTest::class.java, "OcenyWszystkie-subjects-average.html").getGradesSummary(0).blockingGet()
    }

    private val student by lazy {
        getStudentRepo(GradesTest::class.java, "Oceny.json").getGradesSummary(0).blockingGet()
    }

    @Test
    fun getSummaryStd_longPredictedAndFinal() {
        std[0].run {
            assertEquals("Metodologia programowania", name)
            assertEquals("5", predicted)
            assertEquals("6", final)
        }
    }

    @Test
    fun getSummaryAverage_empty() {
        listOf(snp[0], student[0]).map {
            it.run {
                assertEquals("Edukacja dla bezpieczeństwa", name)
                assertEquals("", predicted)
                assertEquals("", final)
            }
        }
    }

    @Test
    fun getSummaryAverage_longFinal() {
        listOf(snp[1], student[1]).map {
            it.run {
                assertEquals("Fizyka", name)
                assertEquals("3", predicted)
                assertEquals("4", final)
            }
        }
    }

    @Test
    fun getSummaryAverage_longPredictedAndFinal() {
        listOf(snp[2], student[2]).map {
            it.run {
                assertEquals("Język angielski", name)
                assertEquals("5", predicted)
                assertEquals("6", final)
            }
        }
    }

    @Test
    fun getSummaryAverage_shortPredictedAndLongFinal() {
        listOf(snp[3], student[3]).map {
            it.run {
                assertEquals("Język polski", name)
                assertEquals("4/5", predicted)
                assertEquals("5", final)
            }
        }
    }

    @Test
    fun getSummaryAverage_shortNegativePredictedAndFinal() {
        listOf(snp[4], student[4]).map {
            it.run {
                assertEquals("Wychowanie fizyczne", name)
                assertEquals("4-", predicted)
                assertEquals("5-", final)
            }
        }
    }
}
