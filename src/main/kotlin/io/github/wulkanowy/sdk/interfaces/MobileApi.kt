package io.github.wulkanowy.sdk.interfaces

import io.github.wulkanowy.sdk.attendance.AttendanceResponse
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
import io.github.wulkanowy.sdk.notes.Note
import io.github.wulkanowy.sdk.notes.NotesRequest
import io.github.wulkanowy.sdk.timetable.Lesson
import io.github.wulkanowy.sdk.timetable.TimetableRequest
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

interface MobileApi {

    @POST("LogAppStart")
    fun logAppStart(@Body logAppStartRequest: ApiRequest): Observable<ApiResponse<String>>

    @POST("Slowniki")
    fun getDictionaries(@Body dictionariesRequest: DictionariesRequest): Observable<ApiResponse<Dictionaries>>

    @POST("PlanLekcjiZeZmianami")
    fun getTimetable(@Body timetableRequest: TimetableRequest): Observable<ApiResponse<List<Lesson>>>

    @POST("Oceny")
    fun getGrades(@Body gradesRequest: GradesRequest): Observable<ApiResponse<List<Grade>>>

    @POST("Sprawdziany")
    fun getExams(@Body examsRequest: ExamsRequest): Observable<ApiResponse<List<Exam>>>

    @POST("UwagiUcznia")
    fun getNotes(@Body notesRequest: NotesRequest): Observable<ApiResponse<List<Note>>>

    @POST("Frekwencje")
    fun getAttendance(@Body attendanceRequest: AttendanceRequest): Observable<ApiResponse<AttendanceResponse>>

    @POST("ZadaniaDomowe")
    fun getHomework(@Body homeworkRequest: HomeworkRequest): Observable<ApiResponse<List<Homework>>>
}
