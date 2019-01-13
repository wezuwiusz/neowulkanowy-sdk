package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.ApiResponse
import io.github.wulkanowy.api.messages.*
import io.reactivex.Single
import retrofit2.http.*

interface MessagesService {

    @GET("NowaWiadomosc.mvc/GetJednostkiUzytkownika")
    fun getUserReportingUnits(): Single<ApiResponse<List<ReportingUnit>>>

    @GET("Adresaci.mvc/GetAdresaci")
    fun getRecipients(@Query("IdJednostkaSprawozdawcza") reportingUnitId: Int, @Query("Rola") role: Int): Single<ApiResponse<List<Recipient>>>

    @GET("Wiadomosc.mvc/GetWiadomosciOdebrane")
    fun getReceived(@Query("dataOd") dateStart: String, @Query("dataDo") dateEnd: String): Single<ApiResponse<List<Message>>>

    @GET("Wiadomosc.mvc/GetWiadomosciWyslane")
    fun getSent(@Query("dataOd") dateStart: String, @Query("dataDo") dateEnd: String): Single<ApiResponse<List<Message>>>

    @GET("Wiadomosc.mvc/GetWiadomosciUsuniete")
    fun getDeleted(@Query("dataOd") dateStart: String, @Query("dataDo") dateEnd: String): Single<ApiResponse<List<Message>>>

    @POST("Wiadomosc.mvc/GetTrescWiadomosci")
    @FormUrlEncoded
    fun getMessage(
            @Field("idWiadomosc") messageId: Int,
            @Field("Folder") folderId: Int,
            @Field("Nieprzeczytana") read: Boolean,
            @Field("idWiadomoscAdresat") id: Int?
    ): Single<ApiResponse<Message>>

    @POST("NowaWiadomosc.mvc/InsertWiadomosc")
    fun sendMessage(@Body sendMessageRequest: SendMessageRequest): Single<ApiResponse<SentMessage>>
}
