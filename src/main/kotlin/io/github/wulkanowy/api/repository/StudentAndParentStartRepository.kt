package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.register.Semester
import io.github.wulkanowy.api.service.StudentAndParentService
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class StudentAndParentStartRepository(
        private val schema: String,
        private val host: String,
        private val symbol: String,
        private val schoolId: String,
        private val studentId: String,
        private val client: OkHttpClient
) {

    private val api by lazy {
        Retrofit.Builder()
                .baseUrl("$schema://uonetplus-opiekun.$host/$symbol/$schoolId/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JspoonConverterFactory.create())
                .client(client)
                .build()
                .create(StudentAndParentService::class.java)
    }

    fun getSemesters(): Single<List<Semester>> {
        return api.getUserInfo(studentId).flatMapObservable { Observable.fromIterable(it.diaries) }
                .flatMapSingle { diary ->
                    api.getDiaryInfo(diary.id, "/$symbol/$schoolId/Oceny.mvc/Wszystkie").map { res ->
                        listOf(1, 2).map { it ->
                            Semester(diary.id, diary.name, if (it == res.semesterNumber) res.semesterId else {
                                if (it < res.semesterNumber) res.semesterId - 1 else res.semesterId + 1
                            }, it)
                        }
                    }
                }.toList().map { it.flatten() }
    }
}
