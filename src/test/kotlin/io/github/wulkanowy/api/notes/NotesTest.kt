package io.github.wulkanowy.api.notes

import io.github.wulkanowy.api.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test


class NotesTest : BaseLocalTest() {

    private val filled by lazy {
        getSnpRepo(NotesTest::class.java, "UwagiOsiagniecia-filled.html").getNotes().blockingGet()
    }

    private val empty by lazy {
        getSnpRepo(NotesTest::class.java, "UwagiOsiagniecia-empty.html").getNotes().blockingGet()
    }

    @Test
    fun getAllNotesTest() {
        assertEquals(3, filled.size)
        assertEquals(0, empty.size)
    }

    @Test
    fun getDateTest() {
        assertEquals(getDate(2016, 10, 1), filled[0].date)
        assertEquals(getDate(2017, 6, 6), filled[2].date)
    }

    @Test
    fun getTeacherTest() {
        assertEquals("Kochański Leszek", filled[0].teacher)
        assertEquals("Jan Kowalski", filled[2].teacher)
    }

    @Test
    fun getTeacherSymbol() {
        assertEquals("KL", filled[0].teacherSymbol)
        assertEquals("JK", filled[2].teacherSymbol)
    }

    @Test
    fun getCategoryTest() {
        assertEquals("Zachowanie na lekcji", filled[0].category)
        assertEquals("Zaangażowanie społeczne", filled[2].category)
    }

    @Test
    fun getContentTest() {
        assertEquals("Przeszkadzanie w prowadzeniu lekcji", filled[0].content)
        assertEquals("Pomoc przy pikniku charytatywnym", filled[2].content)
    }
}
