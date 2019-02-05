package io.github.wulkanowy.api.notes

import io.github.wulkanowy.api.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test

class NotesTest : BaseLocalTest() {

    private val empty by lazy {
        getSnpRepo(NotesTest::class.java, "UwagiOsiagniecia-empty.html").getNotes().blockingGet()
    }

    private val snp by lazy {
        getSnpRepo(NotesTest::class.java, "UwagiOsiagniecia-filled.html").getNotes().blockingGet()
    }

    private val student by lazy {
        getStudentRepo(NotesTest::class.java, "UwagiIOsiagniecia.json").getNotes().blockingGet()
    }

    @Test
    fun getNotesList() {
        assertEquals(3, snp.size)
        assertEquals(3, student.size)
        assertEquals(0, empty.size)
    }

    @Test
    fun getNotes() {
        listOf(snp[0], student[0]).map {
            it.run {
                assertEquals(getDate(2016, 10, 1), date)
                assertEquals("Kochański Leszek", teacher)
                assertEquals("KL", teacherSymbol)
                assertEquals("Zachowanie na lekcji", category)
                assertEquals("Przeszkadzanie w prowadzeniu lekcji", content)
            }
        }
    }
}
