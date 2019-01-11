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
    fun getSummaryTest() {
        assertEquals(5, std.size)
        assertEquals(5, average.size)
    }

    @Test
    fun getNameTest() {
        assertEquals("Metodologia programowania", std[0].name)
        assertEquals("Podstawy przedsiębiorczości", std[1].name)
        assertEquals("Praktyka zawodowa", std[2].name)
        assertEquals("Wychowanie do życia w rodzinie", std[3].name)
        assertEquals("Zachowanie", std[4].name)

        assertEquals("Język angielski", average[0].name)
        assertEquals("Język polski", average[1].name)
        assertEquals("Wiedza o społeczeństwie", average[2].name)
        assertEquals("Wychowanie fizyczne", average[3].name)
        assertEquals("Zachowanie", average[4].name)
    }

    @Test
    fun getPredictedRatingTest() {
        assertEquals("5", std[0].predicted)
        assertEquals("3/4", std[1].predicted)
        assertEquals("", std[2].predicted)
        assertEquals("", std[3].predicted)
        assertEquals("bardzo dobre", std[4].predicted)

        assertEquals("4/5", average[0].predicted)
        assertEquals("", average[1].predicted)
        assertEquals("4", average[2].predicted)
        assertEquals("5", average[3].predicted)
        assertEquals("bardzo dobre", average[4].predicted)
    }

    @Test
    fun getFinalRatingTest() {
        assertEquals("6", std[0].final)
        assertEquals("3", std[1].final)
        assertEquals("6", std[2].final)
        assertEquals("", std[3].final)
        assertEquals("bardzo dobre", std[4].final)

        assertEquals("5", average[0].final)
        assertEquals("4", average[1].final)
        assertEquals("5-", average[2].final)
        assertEquals("6", average[3].final)
        assertEquals("bardzo dobre", average[4].final)
    }
}
