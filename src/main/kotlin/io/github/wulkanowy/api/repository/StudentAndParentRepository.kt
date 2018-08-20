package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.interceptor.LoginInterceptor
import io.github.wulkanowy.api.interfaces.StudentAndParentApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class StudentAndParentRepository(private val host: String,
                                 private val symbol: String,
                                 private val schoolId: String,
                                 private val loginInterceptor: LoginInterceptor
) {

    private val api by lazy { getMobileApi() }

    fun getTimetable(startDate: String, diaryId: Int, studentId: Int) = api.getTimetable(startDate)

    fun getGrades(classificationPeriodId: Int, diaryId: Int, studentId: Int) = api.getGrades(classificationPeriodId)

    fun getExams(startDate: String, diaryId: Int, studentId: Int) = api.getExams(startDate)

    fun getNotes(diaryId: Int, studentId: Int) = api.getNotes()

    fun getAttendance(startDate: String, diaryId: Int, studentId: Int) = api.getAttendance(startDate)

    fun getHomework(startDate: String, diaryId: Int, studentId: Int) = api.getHomework(startDate)

    private fun getMobileApi(): StudentAndParentApi {
        return Retrofit.Builder()
                .baseUrl("$host/$symbol/$schoolId/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JspoonConverterFactory.create())
                .client(OkHttpClient().newBuilder()
                        .addInterceptor(loginInterceptor)
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build()
                )
                .build()
                .create(StudentAndParentApi::class.java)
    }
}
