package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.ApiResponse
import io.github.wulkanowy.api.messages.Message
import io.github.wulkanowy.api.messages.Recipient
import io.github.wulkanowy.api.messages.ReportingUnit
import io.github.wulkanowy.api.messages.SendMessageRequest
import io.github.wulkanowy.api.messages.SentMessage
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface MessagesService {

    @GET(".")
    fun getStart(): Single<String>

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

    @GET("Wiadomosc.mvc/GetAdresaciWiadomosci")
    fun getMessageRecipients(@Query("idWiadomosci") messageId: Int): Single<ApiResponse<List<Recipient>>>

    @GET("Wiadomosc.mvc/GetRoleUzytkownika")
    fun getMessageSender(@Query("idLogin") loginId: Int, @Query("idWiadomosci") messageId: Int): Single<ApiResponse<List<Recipient>>>

    @POST("Wiadomosc.mvc/GetTrescWiadomosci")
    @FormUrlEncoded
    fun getMessage(
        @Field("idWiadomosc") messageId: Int,
        @Field("Folder") folderId: Int,
        @Field("Nieprzeczytana") read: Boolean,
        @Field("idWiadomoscAdresat") id: Int?
    ): Single<ApiResponse<Message>>

    @POST("NowaWiadomosc.mvc/InsertWiadomosc")
    fun sendMessage(
        @Body sendMessageRequest: SendMessageRequest,
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String
    ): Single<ApiResponse<SentMessage>>
}
