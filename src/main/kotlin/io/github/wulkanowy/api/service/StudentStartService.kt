package io.github.wulkanowy.api.service

import retrofit2.http.POST

interface StudentStartService {

    @POST("UczenCache.mvc/Get")
    fun getUserCache()

    @POST("UczenDziennik.mvc/Get")
    fun getDiaries()
}
