package io.github.wulkanowy.api.timetable

import io.github.wulkanowy.api.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TimetableTest : BaseTest() {

    private val table by lazy {
        getSnpRepo(TimetableTest::class.java, "PlanLekcji.html").getTimetable(getLocalDate(2018, 9, 24)).blockingGet()
    }

    @Test
    fun getTimetableTest() {
        assertEquals(10, table.size)
    }

    @Test
    fun getSimpleLesson() {
        table[0].run {
            // poniedziałek, 0
            assertEquals(0, number)
            assertEquals(getDate(2018, 9, 24, 7, 10, 0), start)
            assertEquals(getDate(2018, 9, 24, 7, 55, 0), end)

            assertEquals("Matematyka", subject)
            assertEquals("Rachunek Beata", teacher)
            assertEquals("23", room)

            assertEquals("", group)
            assertEquals("", info)
            assertEquals(false, canceled)
            assertEquals(false, changes)
        }
    }

    @Test
    fun getSimpleLesson_canceled() {
        table[2].run {
            // wtorek, 0
            assertEquals(0, number)
            assertEquals(getDate(2018, 9, 25, 7, 10, 0), start)

            assertEquals("Język polski", subject)
            assertEquals("Polonistka Joanna", teacher)
            assertEquals("W3", room)
            assertEquals("oddział nieobecny - Wycieczka warsztat", info)
            assertEquals(true, canceled)

            assertEquals("", group)
            assertEquals(false, changes)
        }
    }

    @Test
    fun getSimpleLesson_replacementSameTeacher() {
        table[4].run {
            // środa, 0
            assertEquals(0, number)
            assertEquals(getDate(2018, 9, 26, 7, 10, 0), start)

            assertEquals("Język polski", subject)
            assertEquals("Polonistka Joanna", teacher)
            assertEquals("5", room)
            assertEquals("poprzednio: Religia, Polonistka Joanna, sala 3", info)

            assertEquals(false, canceled)
            assertEquals(true, changes)

            assertEquals("", group)
        }
    }

    @Test
    fun getSimpleLesson_replacementDifferentTeacher() {
        table[6].run {
            // czwartek, 0
            assertEquals(0, number)
            assertEquals(getDate(2018, 9, 27, 7, 10, 0), start)

            assertEquals("Wychowanie do życia w rodzinie", subject)
            assertEquals("Telichowska Aleksandra", teacher)
            assertEquals("5", room)
            assertEquals("zastępstwo, poprzednio: Religia, Gliniak Krystian, sala 3", info)

            assertEquals(false, canceled)
            assertEquals(true, changes)

            assertEquals("", group)
        }
    }

    @Test
    fun getGroupLesson() {
        table[8].run {
            // piątek, 0
            assertEquals(0, number)
            assertEquals(getDate(2018, 9, 28, 7, 10, 0), start)

            assertEquals("Fizyka", subject)
            assertEquals("zaw2", group)
            assertEquals("Fizyczny Janusz", teacher)
            assertEquals("19", room)

            assertEquals(false, canceled)
            assertEquals(false, changes)
            assertEquals("", info)
        }
    }

    @Test
    fun getGroupLesson_canceled() {
        table[1].run {
            // poniedziałek, 1
            assertEquals(1, number)
            assertEquals(getDate(2018, 9, 24, 8, 0, 0), start)

            assertEquals("Język angielski", subject)
            assertEquals("J1", group)
            assertEquals("Angielska Amerykanka", teacher)
            assertEquals("24", room)
            assertEquals("uczniowie przychodzą później", info)

            assertEquals(true, canceled)
            assertEquals(false, changes)
        }
    }

    @Test
    fun getGroupLesson_replacementSameTeacher() {
        table[3].run {
            // wtorek, 1
            assertEquals(1, number)
            assertEquals(getDate(2018, 9, 25, 8, 0, 0), start)

            assertEquals("Wychowanie fizyczne", subject)
            assertEquals("zaw2", group)
            assertEquals("Wychowawczy Kazimierz", teacher)
            assertEquals("WG", room)
            assertEquals("poprzednio: Naprawa komputera, Naprawowy Andrzej, sala 32", info)

            assertEquals(false, canceled)
            assertEquals(true, changes)
        }
    }

    @Test
    fun getGroupLesson_replacementDifferentTeacher() {
        table[5].run {
            // środa, 1
            assertEquals(1, number)
            assertEquals(getDate(2018, 9, 26, 8, 0, 0), start)

            assertEquals("Tworzenie i administrowanie bazami danych", subject)
            assertEquals("Kobyliński Leszek", teacher)
            assertEquals("34", room)
            assertEquals("zastępstwo, poprzednio: Tworzenie i administrowanie bazami danych [zaw2], Dębicki Robert, sala 34", info)

            assertEquals(false, canceled)
            assertEquals(true, changes)

            assertEquals("zaw2", group)
        }
    }

    @Test
    fun getLesson_button() {
        table[7].run {
            // czwartek, 1
            assertEquals(1, number)
            assertEquals(getDate(2018, 9, 27, 8, 0, 0), start)

            assertEquals("Język polski", subject)
            assertEquals("Polonistka Joanna", teacher)
            assertEquals("16", room)
            assertEquals("oddział nieobecny, egzamin", info)

            assertEquals(true, canceled)
            assertEquals(false, changes)

            assertEquals("", group)
        }
    }

    @Test
    fun getLesson_emptyOriginal() {
        table[9].run {
            // piątek, 1
            assertEquals(1, number)
            assertEquals(getDate(2018, 9, 28, 8, 0, 0), start)

            assertEquals("Wychowanie fizyczne", subject)
            assertEquals("zaw2", group)
            assertEquals("", teacher)
            assertEquals("G3", room)
            assertEquals("przeniesiona z lekcji 7, 01.12.2017", info)

            assertEquals(true, canceled)
            assertEquals(false, changes)
        }
    }
}
