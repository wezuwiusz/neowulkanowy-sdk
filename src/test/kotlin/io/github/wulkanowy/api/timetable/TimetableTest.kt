package io.github.wulkanowy.api.timetable

import io.github.wulkanowy.api.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TimetableTest : BaseLocalTest() {

    private val snp by lazy {
        getSnpRepo(TimetableTest::class.java, "PlanLekcji.html").getTimetable(getLocalDate(2018, 9, 24)).blockingGet()
    }

    private val student by lazy {
        getStudentRepo(TimetableTest::class.java, "PlanLekcji.json").getTimetable(getLocalDate(2018, 9, 24)).blockingGet()
    }

    @Test
    fun getTimetableTest() {
        assertEquals(13, snp.size)
        assertEquals(13, student.size)
    }

    @Test
    fun getSimpleLesson() {
        listOf(snp[0], student[0]).map {
            it.run {
                // poniedziałek, 0
                assertEquals(0, number)
                assertEquals(getDate(2018, 9, 24, 7, 10, 0), start)
                assertEquals(getDate(2018, 9, 24, 7, 55, 0), end)

                assertEquals("Matematyka", subject)
                assertEquals("Rachunek Beata", teacher)
                assertEquals("23", room)
                assertEquals("", info)
                assertEquals("", subjectOld)
                assertEquals("", teacherOld)
                assertEquals("", roomOld)

                assertEquals("", group)
                assertEquals("", info)
                assertEquals(false, canceled)
                assertEquals(false, changes)
            }
        }
    }

    @Test
    fun getSimpleLesson_canceled() {
        listOf(snp[3], student[3]).map {
            it.run {
                // wtorek, 0
                assertEquals(0, number)
                assertEquals(getDate(2018, 9, 25, 7, 10, 0), start)

                assertEquals("Język polski", subject)
                assertEquals("Polonistka Joanna", teacher)
                assertEquals("W3", room)
                assertEquals("oddział nieobecny - Wycieczka warsztat", info)
                assertEquals("", subjectOld)
                assertEquals("", teacherOld)
                assertEquals("", roomOld)
                assertEquals(true, canceled)

                assertEquals("", group)
                assertEquals(false, changes)
            }
        }
    }

    @Test
    fun getSimpleLesson_replacementSameTeacher() {
        listOf(snp[6], student[6]).map {
            it.run {
                // środa, 0
                assertEquals(0, number)
                assertEquals(getDate(2018, 9, 26, 7, 10, 0), start)

                assertEquals("Język polski", subject)
                assertEquals("Polonistka Joanna", teacher)
                assertEquals("5", room)
                assertEquals("poprzednio: Religia", info)
                assertEquals("Religia", subjectOld)
                assertEquals("Polonistka Joanna", teacherOld)
                assertEquals("3", roomOld)

                assertEquals(false, canceled)
                assertEquals(true, changes)

                assertEquals("", group)
            }
        }
    }

    @Test
    fun getSimpleLesson_replacementDifferentTeacher() {
        listOf(snp[9], student[9]).map {
            it.run {
                // czwartek, 0
                assertEquals(0, number)
                assertEquals(getDate(2018, 9, 27, 7, 10, 0), start)

                assertEquals("Wychowanie do życia w rodzinie", subject)
                assertEquals("Telichowska Aleksandra", teacher)
                assertEquals("5", room)
                assertEquals("zastępstwo, poprzednio: Religia", info)
                assertEquals("Religia", subjectOld)
                assertEquals("Religijny Janusz", teacherOld)
                assertEquals("3", roomOld)

                assertEquals(false, canceled)
                assertEquals(true, changes)

                assertEquals("", group)
            }
        }
    }

    @Test
    fun getGroupLesson() {
        listOf(snp[11], student[11]).map {
            it.run {
                // piątek, 0
                assertEquals(0, number)
                assertEquals(getDate(2018, 9, 28, 7, 10, 0), start)

                assertEquals("Fizyka", subject)
                assertEquals("zaw2", group)
                assertEquals("Fizyczny Janusz", teacher)
                assertEquals("19", room)
                assertEquals("", info)
                assertEquals("", subjectOld)
                assertEquals("", teacherOld)
                assertEquals("", roomOld)

                assertEquals(false, canceled)
                assertEquals(false, changes)
                assertEquals("", info)
            }
        }
    }

    @Test
    fun getGroupLesson_canceled() {
        listOf(snp[1], student[1]).map {
            it.run {
                // poniedziałek, 1
                assertEquals(1, number)
                assertEquals(getDate(2018, 9, 24, 8, 0, 0), start)

                assertEquals("Język angielski", subject)
                assertEquals("J1", group)
                assertEquals("Angielska Amerykanka", teacher)
                assertEquals("24", room)
                assertEquals("uczniowie przychodzą później", info)
                assertEquals("", subjectOld)
                assertEquals("", teacherOld)
                assertEquals("", roomOld)

                assertEquals(true, canceled)
                assertEquals(false, changes)
            }
        }
    }

    @Test
    fun getGroupLesson_replacementSameTeacher() {
        listOf(snp[4], student[4]).map {
            it.run {
                // wtorek, 1
                assertEquals(1, number)
                assertEquals(getDate(2018, 9, 25, 8, 0, 0), start)

                assertEquals("Wychowanie fizyczne", subject)
                assertEquals("zaw2", group)
                assertEquals("Wychowawczy Kazimierz", teacher)
                assertEquals("WG", room)
                assertEquals("poprzednio: Naprawa komputera", info)
                assertEquals("Naprawa komputera", subjectOld)
                assertEquals("Naprawowy Andrzej", teacherOld)
                assertEquals("32", roomOld)

                assertEquals(false, canceled)
                assertEquals(true, changes)
            }
        }
    }

    @Test
    fun getGroupLesson_replacementDifferentTeacher() {
        listOf(snp[7], student[7]).map {
            it.run {
                // środa, 1
                assertEquals(1, number)
                assertEquals(getDate(2018, 9, 26, 8, 0, 0), start)

                assertEquals("Tworzenie i administrowanie bazami danych", subject)
                assertEquals("Kobyliński Leszek", teacher)
                assertEquals("34", room)
                assertEquals("zastępstwo, poprzednio: Tworzenie i administrowanie bazami danych", info)
                assertEquals("Tworzenie i administrowanie bazami danych", subjectOld)
                assertEquals("Dębicki Robert", teacherOld)
                assertEquals("34", roomOld)

                assertEquals(false, canceled)
                assertEquals(true, changes)

                assertEquals("zaw2", group)
            }
        }
    }

    @Test
    fun getLesson_button() {
        listOf(snp[10], student[10]).map {
            it.run {
                // czwartek, 1
                assertEquals(1, number)
                assertEquals(getDate(2018, 9, 27, 8, 0, 0), start)

                assertEquals("Język polski", subject)
                assertEquals("Polonistka Joanna", teacher)
                assertEquals("16", room)
                assertEquals("oddział nieobecny, egzamin", info)
                assertEquals("", subjectOld)
                assertEquals("", teacherOld)
                assertEquals("", roomOld)

                assertEquals(true, canceled)
                assertEquals(false, changes)

                assertEquals("", group)
            }
        }
    }

    @Test
    fun getLesson_emptyOriginal() {
        listOf(snp[12], student[12]).map {
            it.run {
                // piątek, 1
                assertEquals(1, number)
                assertEquals(getDate(2018, 9, 28, 8, 0, 0), start)

                assertEquals("Wychowanie fizyczne", subject)
                assertEquals("zaw2", group)
                assertEquals("", teacher)
                assertEquals("G3", room)
                assertEquals("przeniesiona z lekcji 7, 01.12.2017", info)
                assertEquals("", subjectOld)
                assertEquals("", teacherOld)
                assertEquals("", roomOld)

                assertEquals(true, canceled)
                assertEquals(false, changes)
            }
        }
    }

    @Test
    fun getLesson() {
        listOf(snp[2], student[2]).map {
            it.run {
                // poniedziałek, 2
                assertEquals(2, number)
                assertEquals(getDate(2018, 9, 24, 8, 50, 0), start)

                assertEquals("Geografia", subject)
                assertEquals("", group)
                assertEquals("Światowy Michał", teacher)
                assertEquals("23", room)
                assertEquals("zastępstwo, poprzednio: Religia", info)
                assertEquals("Religia", subjectOld)
                assertEquals("Religijny Janusz", teacherOld)
                assertEquals("23", roomOld)

                assertEquals(false, canceled)
                assertEquals(true, changes)
            }
        }
    }

    @Test
    fun getLesson_invAndChange() {
        listOf(snp[5], student[5]).map {
            it.run {
                // wtorek, 2
                assertEquals(2, number)
                assertEquals(getDate(2018, 9, 25, 8, 50, 0), start)

                assertEquals("Język angielski", subject)
                assertEquals("", group)
                assertEquals("", teacher)
                assertEquals("", room)
                assertEquals("przeniesiona z lekcji 4, 07.03.2019", info)
                assertEquals("Matematyka", subjectOld)
                assertEquals("", teacherOld)
                assertEquals("", roomOld)

                assertEquals(false, canceled)
                assertEquals(true, changes)
            }
        }
    }

    @Test
    fun getSimpleLesson_replacementDifferentTeacherv2() {
        listOf(snp[8], student[8]).map {
            it.run {
                // środa, 2
                assertEquals(2, number)
                assertEquals(getDate(2018, 9, 26, 8, 50, 0), start)

                assertEquals("Język angielski", subject)
                assertEquals("", group)
                assertEquals("", teacher)
                assertEquals("", room)
                assertEquals("Poprzednio: Matematyka (przeniesiona na lekcję 2, 07.03.2019)", info)
                assertEquals("Matematyka", subjectOld)
                assertEquals("", teacherOld)
                assertEquals("", roomOld)

                assertEquals(false, canceled)
                assertEquals(true, changes)
            }
        }
    }
}
