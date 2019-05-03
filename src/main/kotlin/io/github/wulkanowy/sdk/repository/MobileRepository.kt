package io.github.wulkanowy.sdk.repository

import io.github.wulkanowy.sdk.attendance.Attendance
import io.github.wulkanowy.sdk.attendance.AttendanceRequest
import io.github.wulkanowy.sdk.base.ApiRequest
import io.github.wulkanowy.sdk.base.ApiResponse
import io.github.wulkanowy.sdk.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.dictionaries.DictionariesRequest
import io.github.wulkanowy.sdk.exams.Exam
import io.github.wulkanowy.sdk.exams.ExamsRequest
import io.github.wulkanowy.sdk.grades.Grade
import io.github.wulkanowy.sdk.grades.GradesRequest
import io.github.wulkanowy.sdk.homework.Homework
import io.github.wulkanowy.sdk.homework.HomeworkRequest
import io.github.wulkanowy.sdk.interceptor.SignInterceptor
import io.github.wulkanowy.sdk.notes.Note
import io.github.wulkanowy.sdk.notes.NotesRequest
import io.github.wulkanowy.sdk.service.MobileService
import io.github.wulkanowy.sdk.timetable.Lesson
import io.github.wulkanowy.sdk.timetable.TimetableRequest
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MobileRepository(
    private val password: String,
    private val host: String,
    private val symbol: String,
    private val certKey: String,
    private val certificate: String,
    private val reportingUnitSymbol: String
) {

    private val api by lazy { getMobileApi() }

    fun logStart(): Single<ApiResponse<String>> = api.logAppStart(object: ApiRequest() {})

    fun getDictionaries(userId: Int, classificationPeriodId: Int, classId: Int): Single<Dictionaries>
            = api.getDictionaries(DictionariesRequest(userId, classificationPeriodId, classId)).map { it.data }

    fun getTimetable(startDate: String, endDate: String, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Lesson>> {
        return getMobileApi().getTimetable(TimetableRequest(startDate, endDate, classId, classificationPeriodId, studentId)).map { it.data }
    }

    fun getGrades(classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Grade>> {
        return getMobileApi().getGrades(GradesRequest(classId, classificationPeriodId, studentId)).map { it.data }
    }

    fun getExams(startDate: String, endDate: String, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Exam>> {
        return getMobileApi().getExams(ExamsRequest(startDate, endDate, classId, classificationPeriodId, studentId)).map { it.data }
    }

    fun getNotes(classificationPeriodId: Int, studentId: Int): Single<List<Note>> {
        return getMobileApi().getNotes(NotesRequest(classificationPeriodId, studentId)).map { it.data }
    }

    fun getAttendance(startDate: String, endDate: String, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Attendance>> {
        return getMobileApi().getAttendance(AttendanceRequest(startDate, endDate, classId, classificationPeriodId, studentId)).map { it.data?.data }
    }

    fun getHomework(startDate: String, endDate: String, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Homework>> {
        return getMobileApi().getHomework(HomeworkRequest(startDate, endDate, classId, classificationPeriodId, studentId)).map { it.data }
    }

    private fun getMobileApi(): MobileService {
        return Retrofit.Builder()
                .baseUrl("$host/$reportingUnitSymbol/mobile-api/Uczen.v3.Uczen/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient().newBuilder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .addInterceptor(SignInterceptor(password, certificate, certKey))
                        .build()
                )
                .build()
                .create(MobileService::class.java)
    }
}
