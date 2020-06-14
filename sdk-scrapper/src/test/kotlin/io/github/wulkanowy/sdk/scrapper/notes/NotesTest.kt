package io.github.wulkanowy.sdk.scrapper.notes

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class NotesTest : BaseLocalTest() {

    private val student by lazy {
        runBlocking { getStudentRepo(NotesTest::class.java, "UwagiIOsiagniecia.json").getNotes() }
    }

    private val studentPoints by lazy {
        runBlocking { getStudentRepo(NotesTest::class.java, "UwagiIOsiagniecia-points.json").getNotes() }
    }

    @Test
    fun getNotesList() {
        assertEquals(3, student.size)
        assertEquals(3, studentPoints.size)
    }

    @Test
    fun getNotes() {
        with(student[0]) {
            assertEquals(getDate(2016, 10, 1), date)
            assertEquals("Kochański Leszek", teacher)
            assertEquals("KL", teacherSymbol)
            assertEquals("Zachowanie na lekcji", category)
            assertEquals("Przeszkadzanie w prowadzeniu lekcji", content)
            assertEquals(false, showPoints)
            assertEquals(0, points.toIntOrNull() ?: 0)
            assertEquals(Note.CategoryType.NEGATIVE.id, categoryType)
        }
    }

    @Test
    fun getNotes_positive() {
        with(studentPoints[0]) {
            assertEquals(getDate(2020, 2, 18, 22, 58, 49), date)
            assertEquals("Jan Kowalski", teacher)
            assertEquals("JK", teacherSymbol)
            assertEquals("Przygotowanie dodatkowych pomocy naukowych (pozytywna)", category)
            assertEquals("Jan z własnej woli przyniósł baterie do zegara.", content)
            assertEquals(true, showPoints)
            assertEquals(5, points.toInt())
            assertEquals(Note.CategoryType.POSITIVE.id, categoryType)
        }
    }

    @Test
    fun getNotes_neutral() {
        with(studentPoints[1]) {
            assertEquals(getDate(2020, 2, 18, 22, 58, 50), date)
            assertEquals("Kochański Leszek", teacher)
            assertEquals("KL", teacherSymbol)
            assertEquals("Odnotanie neutralnego zachowania ucznia (neutralna)", category)
            assertEquals("Uczeń nic nie zepsuł ani nic nie naprawił", content)
            assertEquals(true, showPoints)
            assertEquals(0, points.toInt())
            assertEquals(Note.CategoryType.NEUTRAL.id, categoryType)
        }
    }

    @Test
    fun getNotes_negative() {
        with(studentPoints[2]) {
            assertEquals(getDate(2020, 2, 19, 14, 12, 52), date)
            assertEquals("Ochocka Zofia", teacher)
            assertEquals("OZ", teacherSymbol)
            assertEquals("Nie zgłoszenie się w umówionym terminie w celu napisania zaległej pracy klasowej (negatywna)", category)
            assertEquals("Uczeń nie przyszedł na zajęcia w celu napisania zaległej kartkówki, pomimo umówienia się z nauczycielem dzień wcześniej.", content)
            assertEquals(true, showPoints)
            assertEquals(-5, points.toInt())
            assertEquals(Note.CategoryType.NEGATIVE.id, categoryType)
        }
    }
}
