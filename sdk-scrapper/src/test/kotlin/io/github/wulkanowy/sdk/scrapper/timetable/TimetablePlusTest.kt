package io.github.wulkanowy.sdk.scrapper.timetable

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class TimetablePlusTest : BaseLocalTest() {

    private val timetableFull by lazy {
        runBlocking {
            getStudentPlusRepo {
                it.enqueue("PlanZajec.json", TimetablePlusTest::class.java)
                it.enqueue("DniWolne.json", TimetablePlusTest::class.java)
            }
                .getTimetable(
                    startDate = getLocalDate(2024, 3, 18),
                    endDate = getLocalDate(2024, 3, 25),
                    studentId = 1,
                    diaryId = 2,
                    unitId = 3,
                )
        }
    }

    private val lessons = timetableFull.lessons
    private val headers = timetableFull.headers

    @Test
    fun getAllTest() {
        assertEquals(9, lessons.size)
        assertEquals(8, headers.size)
    }

    @Test
    fun getTimetableHeaderRange() {
        listOf(3, 4, 5).forEach {
            with(headers[it]) {
                assertEquals(getLocalDate(2024, 3, 18 + it), date)
                assertEquals("Wiosenna przerwa świąteczna", content)
            }
        }
    }

    @Test
    fun getTimetableHeaderDay() {
        with(headers[6]) {
            assertEquals(getLocalDate(2024, 3, 24), date)
            assertEquals("Wielkanoc", content)
        }
        with(headers[7]) {
            assertEquals(getLocalDate(2024, 3, 25), date)
            assertEquals("Poniedziałek Wielkanocny", content)
        }
    }

    @Test
    fun getSimpleLesson() {
        with(lessons[0]) {
            assertEquals(0, number)
            assertEquals(getLocalDateTime(2024, 3, 18, 8, 0, 0), start)
            assertEquals(getLocalDateTime(2024, 3, 18, 8, 45, 0), end)

            assertEquals("Biologia", subject)
            assertEquals("Kowalski Jan", teacher)
            assertEquals("23", room)
            assertEquals("", subjectOld)
            assertEquals("", teacherOld)
            assertEquals("", roomOld)

            assertEquals("", group)
            assertEquals("", info)
            assertEquals(false, canceled)
            assertEquals(false, changes)
        }
    }

    @Test
    fun getLessonWithGroup() {
        with(lessons[1]) {
            assertEquals(0, number)
            assertEquals(getLocalDateTime(2024, 3, 20, 8, 0, 0), start)
            assertEquals(getLocalDateTime(2024, 3, 20, 8, 45, 0), end)

            assertEquals("Religia", subject)
            assertEquals("Błąd Jan", teacher)
            assertEquals("20", room)
            assertEquals("", subjectOld)
            assertEquals("", teacherOld)
            assertEquals("", roomOld)

            assertEquals("|CH", group)
            assertEquals("", info)
            assertEquals(false, canceled)
            assertEquals(false, changes)
        }
    }

    @Test
    fun getLessonWhenTeacherAbsent() {
        with(lessons[2]) {
            assertEquals(0, number)
            assertEquals(getLocalDateTime(2024, 3, 20, 8, 50, 0), start)
            assertEquals(getLocalDateTime(2024, 3, 20, 9, 35, 0), end)

            assertEquals("Zajęcia artystyczne", subject)
            assertEquals("Zwolnieniowy Lekarz", teacher)
            assertEquals("17", room)
            assertEquals("", subjectOld)
            assertEquals("", teacherOld)
            assertEquals("", roomOld)

            assertEquals("", group)
            assertEquals("Nieobecny nauczyciel. Skutek nieobecności: okienko dla uczniów", info)
            assertEquals(true, canceled)
            assertEquals(true, changes)
        }
    }

    @Test
    fun getCancelledLesson() {
        with(lessons[3]) {
            assertEquals(0, number)
            assertEquals(getLocalDateTime(2024, 3, 20, 9, 40, 0), start)
            assertEquals(getLocalDateTime(2024, 3, 20, 10, 25, 0), end)

            assertEquals("Religia", subject)
            assertEquals("Nieobecny Jan", teacher)
            assertEquals("15", room)
            assertEquals("", subjectOld)
            assertEquals("", teacherOld)
            assertEquals("", roomOld)

            assertEquals("", group)
            assertEquals("Oddział nieobecny. Powód nieobecności: powód nieobecności", info)
            assertEquals(true, canceled)
            assertEquals(true, changes)
        }
    }

    @Test
    fun getExchangeLesson() {
        with(lessons[5]) {
            assertEquals(0, number)
            assertEquals(getLocalDateTime(2024, 3, 20, 10, 30, 0), start)
            assertEquals(getLocalDateTime(2024, 3, 20, 11, 15, 0), end)

            assertEquals("Język angielski", subject)
            assertEquals("Nowy Nauczyciel", teacher)
            assertEquals("13", room)
            assertEquals("", subjectOld)
            assertEquals("Nauczyciel Stary", teacherOld)
            assertEquals("", roomOld)

            assertEquals("", group)
            assertEquals("Nieobecny nauczyciel. Zaplanowane jest zastępstwo za nauczyciela: Nauczyciel Stary", info)
            assertEquals(false, canceled)
            assertEquals(true, changes)
        }
    }

    @Test
    fun getMovedLessonFrom() {
        with(lessons[6]) {
            assertEquals(0, number)
            assertEquals(getLocalDateTime(2024, 3, 20, 11, 30, 0), start)
            assertEquals(getLocalDateTime(2024, 3, 20, 12, 15, 0), end)

            assertEquals("Edukacja muzyczna", subject)
            assertEquals("Jan Kowalski", teacher)
            assertEquals("10", room)
            assertEquals("Edukacja muzyczna", subjectOld)
            assertEquals("", teacherOld)
            assertEquals("19", roomOld)

            assertEquals("|CH", group)
            assertEquals("Oddział nieobecny. Zajęcia są przeniesione na: 2024-03-21 w godzinach 14:10-14:55", info)
            assertEquals(true, canceled)
            assertEquals(true, changes)
        }
    }

    @Test
    fun getLessonMovedWithReplacementToTest() {
        with(lessons[4]) {
            assertEquals(0, number)
            assertEquals(getLocalDateTime(2024, 3, 20, 9, 40, 0), start)
            assertEquals(getLocalDateTime(2024, 3, 20, 10, 25, 0), end)

            assertEquals("Edukacja informatyczna", subject)
            assertEquals("Bober Zbigniew", teacher)
            assertEquals("17", room)
            assertEquals("Edukacja informatyczna", subjectOld)
            assertEquals("Kowalski Jan", teacherOld)
            assertEquals("", roomOld)

            assertEquals("", group)
            assertEquals(
                "Zajęcia są przeniesione z dnia 2024-03-20 w godzinach 12:30-13:15. Nieobecny nauczyciel. Zaplanowane jest zastępstwo za nauczyciela: Kowalski Jan",
                info,
            )
            assertEquals(false, canceled)
            assertEquals(true, changes)
        }
    }

    @Test
    fun getLessonMovedWithReplacementFromTest() {
        with(lessons[7]) {
            assertEquals(0, number)
            assertEquals(getLocalDateTime(2024, 3, 20, 12, 30, 0), start)
            assertEquals(getLocalDateTime(2024, 3, 20, 13, 15, 0), end)

            assertEquals("Edukacja informatyczna", subject)
            assertEquals("Jan Kowalski", teacher)
            assertEquals("", room)
            assertEquals("Edukacja informatyczna", subjectOld)
            assertEquals("", teacherOld)
            assertEquals("17", roomOld)

            assertEquals("", group)
            assertEquals("Oddział nieobecny. Zajęcia są przeniesione na: 2024-03-20 w godzinach 09:40-10:25", info)
            assertEquals(true, canceled)
            assertEquals(true, changes)
        }
    }

    @Test
    fun getMovedLessonTo() {
        with(lessons[8]) {
            assertEquals(0, number)
            assertEquals(getLocalDateTime(2024, 3, 20, 14, 10, 0), start)
            assertEquals(getLocalDateTime(2024, 3, 20, 14, 55, 0), end)

            assertEquals("Edukacja muzyczna", subject)
            assertEquals("Jan Kowalski", teacher)
            assertEquals("19", room)
            assertEquals("Edukacja muzyczna", subjectOld)
            assertEquals("", teacherOld)
            assertEquals("10", roomOld)

            assertEquals("|CH", group)
            assertEquals("Zajęcia są przeniesione z dnia 2024-03-20 w godzinach 11:30-12:15", info)
            assertEquals(false, canceled)
            assertEquals(true, changes)
        }
    }
}
