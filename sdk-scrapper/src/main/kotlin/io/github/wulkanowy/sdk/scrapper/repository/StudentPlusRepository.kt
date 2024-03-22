package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.attendance.Absent
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusePlusRequest
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusePlusRequestItem
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusePlusResponseItem
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceExcusesPlusResponse
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummary
import io.github.wulkanowy.sdk.scrapper.attendance.SentExcuseStatus
import io.github.wulkanowy.sdk.scrapper.conferences.Conference
import io.github.wulkanowy.sdk.scrapper.exams.Exam
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanClientError
import io.github.wulkanowy.sdk.scrapper.getEncodedKey
import io.github.wulkanowy.sdk.scrapper.grades.Grades
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesList
import io.github.wulkanowy.sdk.scrapper.grades.mapGradesSummary
import io.github.wulkanowy.sdk.scrapper.handleErrors
import io.github.wulkanowy.sdk.scrapper.homework.Homework
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.notes.Note
import io.github.wulkanowy.sdk.scrapper.notes.NoteCategory
import io.github.wulkanowy.sdk.scrapper.register.AuthorizePermissionPlusRequest
import io.github.wulkanowy.sdk.scrapper.register.RegisterStudent
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.github.wulkanowy.sdk.scrapper.school.School
import io.github.wulkanowy.sdk.scrapper.school.Teacher
import io.github.wulkanowy.sdk.scrapper.service.StudentPlusService
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.Lesson
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableDayHeader
import io.github.wulkanowy.sdk.scrapper.timetable.mapCompletedLessons
import io.github.wulkanowy.sdk.scrapper.toFormat
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.time.LocalDate
import java.time.Month

internal class StudentPlusRepository(
    private val api: StudentPlusService,
) {

    private fun LocalDate.toISOFormat(): String = toFormat("yyyy-MM-dd'T00:00:00'")

    suspend fun authorizePermission(pesel: String, studentId: Int, diaryId: Int, unitId: Int): Boolean {
        runCatching {
            api.authorize(
                AuthorizePermissionPlusRequest(
                    key = getEncodedKey(studentId, diaryId, unitId),
                    pesel = pesel,
                ),
            )
        }.onFailure {
            if (it is VulcanClientError && it.httpCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                if ("odrzucona" in it.message.orEmpty()) {
                    return false
                }
            }
        }.getOrThrow()
        return true
    }

    suspend fun getStudent(studentId: Int, diaryId: Int, unitId: Int): RegisterStudent? {
        return api.getContext().students.find {
            it.key == getEncodedKey(studentId, diaryId, unitId)
        }?.let {
            RegisterStudent(
                studentId = studentId,
                studentName = it.studentName.substringBefore(" "),
                studentSecondName = "", //
                studentSurname = it.studentName.substringAfterLast(" "),
                className = it.className,
                classId = 0, //
                isParent = it.opiekunUcznia,
                semesters = listOf(), //
                isAuthorized = !it.isAuthorizationRequired,
                isEduOne = true,
            )
        }
    }

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate?, studentId: Int, diaryId: Int, unitId: Int): List<Attendance> {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val from = startDate.toISOFormat()
        val to = endDate?.toISOFormat() ?: startDate.plusDays(7).toISOFormat()

        val attendanceItems = api.getAttendance(key = key, from = from, to = to)
        val sentExcuses = api.getExcuses(key = key, from = from, to = to)

        return attendanceItems.onEach {
            it.category = AttendanceCategory.getCategoryById(it.categoryId)

            val sentExcuse = it.getMatchingExcuse(sentExcuses)

            it.excusable = it.isExcusable(sentExcuses.isExcusesActive, sentExcuse)
            if (sentExcuse != null) it.excuseStatus = SentExcuseStatus.getByValue(sentExcuse.status)
        }
    }

    suspend fun getAttendanceSummary(studentId: Int, diaryId: Int, unitId: Int): List<AttendanceSummary> {
        val summaries = api.getAttendanceSummary(getEncodedKey(studentId, diaryId, unitId))

        val stats = summaries.items.associate { it.id to it.months }
        val getMonthValue = fun(type: Int, month: Int): Int {
            return stats[type]?.find { it.month == month }?.value ?: 0
        }

        return (1..12).map {
            AttendanceSummary(
                month = Month.of(it),
                presence = getMonthValue(AttendanceCategory.PRESENCE.id, it),
                absence = getMonthValue(AttendanceCategory.ABSENCE_UNEXCUSED.id, it),
                absenceExcused = getMonthValue(AttendanceCategory.ABSENCE_EXCUSED.id, it),
                absenceForSchoolReasons = getMonthValue(AttendanceCategory.ABSENCE_FOR_SCHOOL_REASONS.id, it),
                lateness = getMonthValue(AttendanceCategory.UNEXCUSED_LATENESS.id, it),
                latenessExcused = getMonthValue(AttendanceCategory.EXCUSED_LATENESS.id, it),
                exemption = getMonthValue(AttendanceCategory.EXEMPTION.id, it),
            )
        }.filterNot { summary ->
            summary.absence == 0 &&
                summary.absenceExcused == 0 &&
                summary.absenceForSchoolReasons == 0 &&
                summary.exemption == 0 &&
                summary.lateness == 0 &&
                summary.latenessExcused == 0 &&
                summary.presence == 0
        }
    }

    private fun Attendance.getMatchingExcuse(sentExcuses: AttendanceExcusesPlusResponse): AttendanceExcusePlusResponseItem? {
        return sentExcuses.excuses.find { excuse ->
            excuse.dayDate == date && (excuse.lessonNumber == number || excuse.lessonNumber == null)
        }
    }

    private fun Attendance.isExcusable(isExcusesActive: Boolean, sentExcuse: AttendanceExcusePlusResponseItem?): Boolean {
        val isUnexcused = category == AttendanceCategory.ABSENCE_UNEXCUSED
        val isUnexcusedLateness = category == AttendanceCategory.UNEXCUSED_LATENESS
        val isStatusMatch = isUnexcused || isUnexcusedLateness

        return isStatusMatch && isExcusesActive && sentExcuse == null
    }

    suspend fun excuseForAbsence(absents: List<Absent>, content: String?, studentId: Int, diaryId: Int, unitId: Int): Boolean {
        api.excuseForAbsence(
            body = AttendanceExcusePlusRequest(
                key = getEncodedKey(studentId, diaryId, unitId),
                content = content.orEmpty(),
                excuses = absents.map {
                    AttendanceExcusePlusRequestItem(
                        date = it.date.toFormat("yyyy-MM-dd'T'HH:mm:ss"),
                        lessonHourId = it.timeId,
                    )
                },
            ),
        ).handleErrors()

        return true
    }

    suspend fun getCompletedLessons(startDate: LocalDate, endDate: LocalDate?, studentId: Int, diaryId: Int, unitId: Int): List<CompletedLesson> {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val context = api.getContext()
        val studentConfig = context.students.find { it.key == key }?.config

        if (studentConfig?.showCompletedLessons != true) {
            throw FeatureDisabledException("Widok lekcji zrealizowanych został wyłączony przez Administratora szkoły")
        }

        return api.getCompletedLessons(
            key = key,
            status = 1,
            from = startDate.toISOFormat(),
            to = endDate?.toISOFormat() ?: startDate.plusDays(7).toISOFormat(),
        ).mapCompletedLessons(startDate, endDate)
    }

    suspend fun getRegisteredDevices(): List<Device> = api.getRegisteredDevices()

    suspend fun getToken(): TokenResponse {
        val res = api.getDeviceRegistrationToken()
        return res.copy(
            qrCodeImage = Jsoup.parse(res.qrCodeImage)
                .select("img")
                .attr("src")
                .split("data:image/png;base64,")[1],
        )
    }

    suspend fun getGrades(semesterId: Int, studentId: Int, diaryId: Int, unitId: Int): Grades {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val res = api.getGrades(key, semesterId)

        return Grades(
            details = res.mapGradesList(),
            summary = res.mapGradesSummary(),
            descriptive = res.gradesDescriptive,
            isAverage = res.isAverage,
            isPoints = res.isPoints,
            isForAdults = res.isForAdults,
            type = res.type,
        )
    }

    suspend fun getExams(startDate: LocalDate, endDate: LocalDate?, studentId: Int, diaryId: Int, unitId: Int): List<Exam> {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val examsHomeworkRes = api.getExamsAndHomework(
            key = key,
            from = startDate.toISOFormat(),
            to = endDate?.toISOFormat(),
        )

        return examsHomeworkRes.filter { it.type != 4 }.map { exam ->
            val examDetailsRes = api.getExamDetails(key, exam.id)
            Exam(
                entryDate = exam.date,
                subject = exam.subject,
                type = exam.type,
                description = examDetailsRes.description,
                teacher = examDetailsRes.teacher,
            ).apply {
                typeName = when (exam.type) {
                    1 -> "Sprawdzian"
                    2 -> "Kartkówka"
                    else -> "Praca klasowa"
                }
                date = exam.date
                teacherSymbol = ""
            }
        }
    }

    suspend fun getHomework(startDate: LocalDate, endDate: LocalDate?, studentId: Int, diaryId: Int, unitId: Int): List<Homework> {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val examsHomeworkRes = api.getExamsAndHomework(
            key = key,
            from = startDate.toISOFormat(),
            to = endDate?.toISOFormat(),
        )

        return examsHomeworkRes.filter { it.type == 4 }.map { homework ->
            val homeworkDetailsRes = api.getHomeworkDetails(key, homework.id)
            Homework(
                homeworkId = homework.id,
                subject = homework.subject,
                teacher = homeworkDetailsRes.teacher,
                content = homeworkDetailsRes.description,
                date = homework.date,
                entryDate = homework.date,
                status = homeworkDetailsRes.status,
                isAnswerRequired = homeworkDetailsRes.isAnswerRequired,
            ).apply {
                teacherSymbol = ""
            }
        }
    }

    suspend fun getTimetable(startDate: LocalDate, endDate: LocalDate?, studentId: Int, diaryId: Int, unitId: Int): Timetable {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val defaultEndDate = (endDate ?: startDate.plusDays(7))
        val lessons = api.getTimetable(
            key = key,
            from = startDate.toISOFormat(),
            to = defaultEndDate?.toISOFormat(),
        ).map { lesson ->
            Lesson(
                number = 0,
                start = lesson.godzinaOd,
                end = lesson.godzinaDo,
                date = lesson.godzinaOd.toLocalDate(),
                subject = lesson.przedmiot,
                subjectOld = lesson.zmiany.find { !it.zajecia.isNullOrBlank() }?.zajecia.orEmpty(),
                group = lesson.podzial.orEmpty(),
                room = lesson.sala,
                roomOld = lesson.zmiany.find { !it.sala.isNullOrBlank() }?.sala.orEmpty(),
                teacher = lesson.prowadzacy,
                teacherOld = lesson.zmiany.find { !it.prowadzacy.isNullOrBlank() }?.prowadzacy.orEmpty(),
                info = buildString {
                    lesson.zmiany.forEach {
                        when (it.typProwadzacego) {
                            0 -> {
                                if (it.zmiana != 6) {
                                    append("Oddział nieobecny. ")
                                }
                            }

                            1 -> {
                                if (it.zmiana != 6) {
                                    append("Nieobecny nauczyciel. ")
                                }
                            }
                        }

                        when (it.zmiana) {
                            1 -> append("Skutek nieobecności: ${it.informacjeNieobecnosc}")
                            2 -> append(it.informacjeNieobecnosc) // todo
                            3 -> append(it.informacjeNieobecnosc) // todo
                            4 -> append("Powód nieobecności: ${it.informacjeNieobecnosc}")

                            // przeniesienie z dnia
                            5 -> {
                                append("Zajęcia są przeniesione na: ")
                                append(it.dzien?.toLocalDate())
                                append(" w godzinach ")
                                append(it.godzinaOd?.toLocalTime())
                                append("-")
                                append(it.godzinaDo?.toLocalTime())
                            }

                            // przeniesienie na dzień
                            6 -> {
                                append("Zajęcia są przeniesione z dnia ")
                                append(it.dzien?.toLocalDate())
                                append(" w godzinach ")
                                append(it.godzinaOd?.toLocalTime())
                                append("-")
                                append(it.godzinaDo?.toLocalTime())
                            }

                            // zastępstwo nauczyciela
                            7 -> append("Zaplanowane jest zastępstwo za nauczyciela: ${it.prowadzacy}")
                        }
                        append(". ")
                    }
                }.trim().trim('.'),
                changes = lesson.adnotacja == REPLACEMENT || lesson.adnotacja == RELOCATION || lesson.zmiany.isNotEmpty(),
                canceled = lesson.adnotacja == CANCELLATION || lesson.zmiany.any { it.zmiana == 1 || it.zmiana == 4 || it.zmiana == 5 },
            )
        }.sortedBy { it.start }

        val headers = api.getTimetableFreeDays(
            key = key,
            from = startDate.toISOFormat(),
            to = endDate?.toISOFormat(),
        )
        val days = (startDate.toEpochDay()..defaultEndDate.toEpochDay()).map(LocalDate::ofEpochDay)
        val processedHeaders = days.map { processedDate ->
            val exactMatch = headers.find {
                it.dataOd.toLocalDate() == processedDate && it.dataDo.toLocalDate() == processedDate
            }
            val rangeMatch = headers.find {
                processedDate in it.dataOd.toLocalDate()..it.dataDo.toLocalDate()
            }
            TimetableDayHeader(
                date = processedDate,
                content = (exactMatch ?: rangeMatch)?.nazwa.orEmpty(),
            )
        }

        return Timetable(
            lessons = lessons,
            headers = processedHeaders,
            // todo
            additional = emptyList(),
        )
    }

    companion object {
        const val REPLACEMENT = 1
        const val RELOCATION = 2
        const val CANCELLATION = 3
    }

    suspend fun getNotes(studentId: Int, diaryId: Int, unitId: Int): List<Note> {
        val key = getEncodedKey(studentId, diaryId, unitId)
        return api.getNotes(key)
            .map {
                it.copy(
                    category = it.category.orEmpty(),
                    categoryType = NoteCategory.UNKNOWN.id,
                ).apply {
                    teacherSymbol = ""
                }
            }
            .sortedWith(compareBy({ it.date }, { it.category }))
    }

    suspend fun getConferences(studentId: Int, diaryId: Int, unitId: Int): List<Conference> {
        val key = getEncodedKey(studentId, diaryId, unitId)
        return api.getConferences(key)
    }

    suspend fun getSemesters(studentId: Int, diaryId: Int, unitId: Int): List<Semester> {
        val key = getEncodedKey(studentId, diaryId, unitId)
        val context = api.getContext()
        val student = context.students.find { it.key == key }
        val level = student?.className.orEmpty().takeWhile { it.isDigit() }
        return api.getSemesters(key, diaryId).map {
            Semester(
                diaryId = diaryId,
                diaryName = student?.className.orEmpty(),
                className = student?.className?.replace(level, ""),
                schoolYear = it.dataOd.toLocalDate().year,
                semesterId = it.id,
                semesterNumber = it.numerOkresu,
                start = it.dataOd.toLocalDate(),
                end = it.dataDo.toLocalDate(),
                unitId = unitId,
                classId = 0,
                kindergartenDiaryId = 0,
            )
        }
    }

    suspend fun getTeachers(): List<Teacher> = api.getTeachers().teachers.map {
        Teacher(
            name = "${it.firstName} ${it.lastName}".trim(),
            subject = it.subject,
        )
    }

    suspend fun getSchool(): School = api.getSchool().let {
        val streetNumber = it.buildingNumber + it.apartmentNumber.takeIf(String::isNotEmpty)?.let { "/$it" }.orEmpty()
        val name = buildString {
            append(it.name)
            if (it.number.isNotEmpty()) {
                append(" nr ${it.number}")
            }
            if (it.patron.isNotEmpty()) {
                append(" im. ${it.patron}")
            }
            if (it.town.isNotEmpty()) {
                append(" w ${it.town}")
            }
        }
        School(
            name = name,
            address = "${it.street} $streetNumber, ${it.postcode} ${it.town}",
            contact = it.workPhone,
            headmaster = it.headmasters.firstOrNull().orEmpty(),
            pedagogue = "",
        )
    }
}
