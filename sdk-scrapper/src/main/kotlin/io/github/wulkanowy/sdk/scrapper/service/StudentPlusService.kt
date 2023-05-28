package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.conferences.Conference
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.timetable.CacheResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

internal interface StudentPlusService {

    @GET
    suspend fun getStart(@Url url: String): String

    @GET("api/Cache")
    suspend fun getUserCache(): CacheResponse

    @GET("api/Frekwencja")
    suspend fun getAttendance(
        @Query("key") key: String,
        @Query("dataOd") from: String,
        @Query("dataDo") to: String,
    ): List<Attendance>

    @GET("api/ZarejestrowaneUrzadzenia")
    suspend fun getRegisteredDevices(): List<Device>

    @GET("api/Zebrania")
    suspend fun getConferences(): List<Conference>
}
