package io.github.wulkanowy.sdk.hebe

import io.github.wulkanowy.sdk.hebe.models.AttendanceSummary
import io.github.wulkanowy.sdk.hebe.models.CompletedLesson
import io.github.wulkanowy.sdk.hebe.models.Exam
import io.github.wulkanowy.sdk.hebe.models.Grade
import io.github.wulkanowy.sdk.hebe.models.GradeAverage
import io.github.wulkanowy.sdk.hebe.models.GradeSummary
import io.github.wulkanowy.sdk.hebe.models.Homework
import io.github.wulkanowy.sdk.hebe.models.Lesson
import io.github.wulkanowy.sdk.hebe.models.LuckyNumber
import io.github.wulkanowy.sdk.hebe.models.Mailbox
import io.github.wulkanowy.sdk.hebe.models.Meeting
import io.github.wulkanowy.sdk.hebe.models.Message
import io.github.wulkanowy.sdk.hebe.models.Subject
import io.github.wulkanowy.sdk.hebe.models.Teacher
import io.github.wulkanowy.sdk.hebe.models.TimetableFull
import io.github.wulkanowy.sdk.hebe.models.TimetableHeader
import io.github.wulkanowy.sdk.hebe.register.RegisterDevice
import io.github.wulkanowy.sdk.hebe.register.StudentInfo
import io.github.wulkanowy.sdk.hebe.repository.RepositoryManager
import io.github.wulkanowy.signer.hebe.generateKeyPair
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class Hebe {

    private val resettableManager = resettableManager()

    var logLevel = HttpLoggingInterceptor.Level.BASIC
        set(value) {
            field = value
            resettableManager.reset()
        }

    var keyId = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var privatePem = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var baseUrl = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var schoolId = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var pupilId = -1
        set(value) {
            field = value
            resettableManager.reset()
        }

    var deviceModel = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    private val appInterceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf()

    fun addInterceptor(interceptor: Interceptor, network: Boolean = false) {
        appInterceptors.add(interceptor to network)
    }

    private val serviceManager by resettableLazy(resettableManager) {
        RepositoryManager(
            logLevel = logLevel,
            keyId = keyId,
            privatePem = privatePem,
            deviceModel = deviceModel,
        ).apply {
            appInterceptors.forEach { (interceptor, isNetwork) ->
                setInterceptor(interceptor, isNetwork)
            }
        }
    }

    private val routes by resettableLazy(resettableManager) { serviceManager.getRoutesRepository() }

    private val studentRepository by resettableLazy(resettableManager) {
        serviceManager.getStudentRepository(
            baseUrl = baseUrl,
            schoolId = schoolId,
        )
    }

    suspend fun register(token: String, pin: String, symbol: String, firebaseToken: String? = null): RegisterDevice {
        val (publicPem, privatePem, publicHash) = generateKeyPair()

        this.keyId = publicHash
        this.privatePem = privatePem

        val envelope = serviceManager
            .getRegisterRepository(
                baseUrl = routes.getRouteByToken(token),
                symbol = symbol,
            ).register(
                firebaseToken = firebaseToken,
                token = token,
                pin = pin,
                certificatePem = publicPem,
                certificateId = publicHash,
                deviceModel = deviceModel,
            )

        return RegisterDevice(
            loginId = envelope.loginId,
            restUrl = envelope.restUrl,
            userLogin = envelope.userLogin,
            userName = envelope.userName,
            certificateHash = publicHash,
            privatePem = privatePem,
        )
    }

    suspend fun getStudents(url: String): List<StudentInfo> = serviceManager
        .getRegisterRepository(url)
        .getStudentInfo()

    suspend fun getGrades(periodId: Int): List<Grade> = studentRepository.getGrades(
        pupilId = pupilId,
        periodId = periodId,
    )

    suspend fun getGradesSummary(periodId: Int): List<GradeSummary> = studentRepository.getGradesSummary(
        pupilId = pupilId,
        periodId = periodId,
    )

    suspend fun getGradesAverage(periodId: Int): List<GradeAverage> = studentRepository.getGradesAverage(
        pupilId = pupilId,
        periodId = periodId,
    )

    suspend fun getExams(startDate: LocalDate, endDate: LocalDate): List<Exam> = studentRepository.getExams(
        pupilId = pupilId,
        startDate = startDate,
        endDate = endDate,
    )

    suspend fun getPeriods(
        url: String,
    ): List<StudentInfo.Period> = serviceManager
        .getRegisterRepository(url)
        .getStudentInfo()
        .first()
        .periods

    suspend fun getTeachers(periodId: Int): List<Teacher> = studentRepository.getTeachers(
        pupilId = pupilId,
        periodId = periodId,
    )

    suspend fun getMessages(messageBoxId: String, folder: Int): List<Message> = studentRepository.getMessages(messageBoxId, folder)

    suspend fun getMailboxes(): List<Mailbox> = studentRepository.getMailboxes()

    suspend fun getRecipients(mailboxKey: String) = studentRepository.getRecipients(mailboxKey)

    suspend fun getMeetings(pupilId: Int, startDate: LocalDate): List<Meeting> = studentRepository.getMeetings(
        pupilId = pupilId,
        startDate = startDate,
    )

    suspend fun getSchedule(pupilId: Int, startDate: LocalDate, endDate: LocalDate): TimetableFull {
        val lessons = arrayListOf<Lesson>()
        val headers = arrayListOf<TimetableHeader>()
        val days = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays()
        val vacations = studentRepository.getVacations(
            pupilId = pupilId,
            startDate = startDate,
            endDate = endDate,
        )

        for (i in 0..<days) {
            val currentDate = startDate.plusDays(i)
            studentRepository
                .getSchedule(
                    pupilId = pupilId,
                    startDate = currentDate,
                    // the API ignores the endDate completely
                    endDate = currentDate,
                ).forEach {
                    lessons.add(it)
                }

            val vacation = vacations.find {
                currentDate >= it.dateFrom.date && currentDate <= it.dateTo.date
            }
            headers.add(
                TimetableHeader(
                    content = vacation?.name ?: "",
                    date = startDate.plusDays(i),
                ),
            )
        }

        val changes = studentRepository.getTimetableChanges(
            pupilId = pupilId,
            startDate = startDate,
            endDate = endDate,
        )

        return TimetableFull(
            lessons = lessons.toList(),
            headers = headers.toList(),
            changes = changes,
        )
    }

    suspend fun getAttendanceSummary(pupilId: Int, startDate: LocalDate, endDate: LocalDate, subjectId: Int): AttendanceSummary {
        val completedLessons = getCompletedLessons(pupilId, startDate, endDate, subjectId)
        var presences = 0
        var absences = 0
        var legalAbsences = 0
        var latenesses = 0
        var justifiedAbsences = 0
        var exemptions = 0
        var justifiedLatenesses = 0
        completedLessons
            .forEach { lesson ->
                if (lesson.presenceType != null) {
                    when (lesson.presenceType.categoryId) {
                        1 -> presences++
                        6 -> legalAbsences++
                        7 -> exemptions++
                        2 -> absences++
                        3 -> justifiedAbsences++
                        4 -> latenesses++
                        5 -> justifiedLatenesses++
                    }
                }
            }

        return AttendanceSummary(
            month = startDate.month,
            presence = presences,
            absence = absences,
            absenceExcused = justifiedAbsences,
            absenceForSchoolReasons = legalAbsences,
            lateness = latenesses,
            latenessExcused = justifiedLatenesses,
            exemption = exemptions,
        )
    }

    suspend fun getAttendanceSummaryForWholeYear(pupilId: Int, startDate: LocalDate, endDate: LocalDate, subjectId: Int): List<AttendanceSummary> {
        val months = ChronoUnit.MONTHS.between(startDate, endDate)
        val summaries = arrayListOf<AttendanceSummary>()

        for (i in 0..<months) {
            summaries.add(
                getAttendanceSummary(
                    pupilId,
                    startDate.plusMonths(i).minusDays(startDate.dayOfMonth.toLong() - 1),
                    startDate.plusMonths(i + 1).minusDays(startDate.dayOfMonth.toLong() - 1),
                    subjectId,
                ),
            )
        }

        return summaries
    }

    suspend fun getCompletedLessons(pupilId: Int, startDate: LocalDate, endDate: LocalDate, subjectId: Int = -1): List<CompletedLesson> {
        var completedLessons = studentRepository
            .getCompletedLessons(
                pupilId = pupilId,
                startDate = startDate,
                endDate = endDate,
            )

        if (subjectId != -1) {
            completedLessons = completedLessons.filter {
                it.subject?.id == subjectId
            }
        }

        return completedLessons
    }

    suspend fun getNotes(pupilId: Int) = studentRepository
        .getNotes(pupilId = pupilId)

    suspend fun getSubjects(pupilId: Int, periodId: Int): List<Subject> = studentRepository
        .getGrades(
            pupilId = pupilId,
            periodId = periodId,
        ).map {
            Subject(
                id = it.column.subject.id,
                name = it.column.subject.name,
            )
        }.distinctBy { it.id }

    suspend fun getHomework(pupilId: Int, startDate: LocalDate, endDate: LocalDate): List<Homework> = studentRepository
        .getHomework(
            pupilId = pupilId,
            startDate = startDate,
            endDate = endDate,
        )

    suspend fun getLuckyNumber(pupilId: Int, constituentId: Int, day: LocalDate = LocalDate.now()): LuckyNumber = studentRepository
        .getLuckyNumber(pupilId, constituentId, day)

    suspend fun setMessageStatus(pupilId: Int?, boxKey: String, messageKey: String, status: Int): Boolean? = studentRepository.setMessageStatus(
        pupilId = pupilId,
        boxKey = boxKey,
        messageKey = messageKey,
        status = status,
    )
}
