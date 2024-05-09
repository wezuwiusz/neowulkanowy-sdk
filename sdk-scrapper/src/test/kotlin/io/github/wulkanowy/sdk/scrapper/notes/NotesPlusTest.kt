package io.github.wulkanowy.sdk.scrapper.notes

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class NotesPlusTest : BaseLocalTest() {

    private val notes by lazy {
        runBlocking {
            getStudentPlusRepo {
                it.enqueue("Context-all-enabled.json", RegisterTest::class.java)
                it.enqueue("UwagiPlus.json")
            }.getNotes(1, 2, 3)
        }
    }

    @Test
    fun getAllNotesTest() {
        assertEquals(2, notes.size)
    }

    @Test
    fun getFullNote() {
        with(notes[0]) {
            assertEquals(getDate(2024, 3, 19, 22, 47, 20), date)
            assertEquals("Jan Kowalski", teacher)
            assertEquals("", teacherSymbol)
            assertEquals("Kultura osobista", category)
            assertEquals("to jest treść uwagi", content)
            assertEquals(false, showPoints)
            assertEquals(0, points.toIntOrNull() ?: 0)
            assertEquals(NoteCategory.UNKNOWN.id, categoryType)
        }
    }

    @Test
    fun getSimpleNote() {
        with(notes[1]) {
            assertEquals(getDate(2024, 3, 19, 22, 52, 21), date)
            assertEquals("Jan Kowalski", teacher)
            assertEquals("", teacherSymbol)
            assertEquals("", category)
            assertEquals("treść kolejnej uwagi", content)
            assertEquals(false, showPoints)
            assertEquals(0, points.toIntOrNull() ?: 0)
            assertEquals(NoteCategory.UNKNOWN.id, categoryType)
        }
    }
}
