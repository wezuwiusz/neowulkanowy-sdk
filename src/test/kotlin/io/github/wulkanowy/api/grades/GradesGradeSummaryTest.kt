package io.github.wulkanowy.api.grades

import io.github.wulkanowy.api.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesGradeSummaryTest : BaseLocalTest() {

    private val std by lazy {
        getSnpRepo(GradesTest::class.java, "OcenyWszystkie-subjects.html").getGradesSummary(0).blockingGet()
    }

    private val average by lazy {
        getSnpRepo(GradesTest::class.java, "OcenyWszystkie-subjects-average.html").getGradesSummary(0).blockingGet()
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
        average[0].run {
            assertEquals("Język angielski", name)
            assertEquals("", predicted)
            assertEquals("", final)
        }
    }

    @Test
    fun getSummaryAverage_longFinal() {
        average[1].run {
            assertEquals("Język polski", name)
            assertEquals("", predicted)
            assertEquals("4", final)
        }
    }

    @Test
    fun getSummaryAverage_longPredictedAndFinal() {
        average[2].run {
            assertEquals("Wiedza o społeczeństwie", name)
            assertEquals("5", predicted)
            assertEquals("6", final)
        }
    }

    @Test
    fun getSummaryAverage_shortPredictedAndLongFinal() {
        average[3].run {
            assertEquals("Wychowanie fizyczne", name)
            assertEquals("4/5", predicted)
            assertEquals("5", final)
        }
    }

    @Test
    fun getSummaryAverage_shortNegativePredictedAndFinal() {
        average[4].run {
            assertEquals("Zachowanie", name)
            assertEquals("4-", predicted)
            assertEquals("5-", final)
        }
    }
}
