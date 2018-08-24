package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.ClientCreator
import io.github.wulkanowy.api.interceptor.StudentAndParentInterceptor
import io.github.wulkanowy.api.interfaces.StudentAndParentApi
import io.github.wulkanowy.api.register.Semester
import io.reactivex.Single
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.net.CookieManager

class StudentAndParentStartRepository(
        private val schema: String,
        private val host: String,
        private val symbol: String,
        private val schoolId: String,
        private val studentId: String,
        private val cookies: CookieManager,
        private val client: ClientCreator
) {

    fun getSemesters(): Single<List<Semester>> {
        return Single.just(getClient("").getUserInfo(studentId).blockingGet().diaries.map { diary ->
            val s = getClient(diary.id).getGrades(0).blockingGet()
            listOf(1, 2).map { it ->
                Semester(diary.id, diary.name, if (it == s.semesterNumber) s.semesterId else {
                    if (it < s.semesterNumber) s.semesterId - 1 else s.semesterId + 1
                }, it)
            }
        }.flatten())
    }

    private fun getClient(diaryId: String): StudentAndParentApi {
        return Retrofit.Builder()
                .baseUrl("$schema://uonetplus-opiekun.$host/$symbol/$schoolId/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JspoonConverterFactory.create())
                .client(client.addInterceptor(StudentAndParentInterceptor(cookies, host, diaryId, studentId)).getClient())
                .build()
                .create(StudentAndParentApi::class.java)
    }
}
