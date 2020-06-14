package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.messages.DeleteMessageRequest
import io.github.wulkanowy.sdk.scrapper.messages.Message
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.ReportingUnit
import io.github.wulkanowy.sdk.scrapper.messages.SendMessageRequest
import io.github.wulkanowy.sdk.scrapper.messages.SentMessage
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface MessagesService {

    @GET(".")
    suspend fun getStart(): String

    @GET("NowaWiadomosc.mvc/GetJednostkiUzytkownika")
    suspend fun getUserReportingUnits(): ApiResponse<List<ReportingUnit>>

    @GET("Adresaci.mvc/GetAdresaci")
    suspend fun getRecipients(@Query("IdJednostkaSprawozdawcza") reportingUnitId: Int, @Query("Rola") role: Int): ApiResponse<List<Recipient>>

    @GET("Wiadomosc.mvc/GetWiadomosciOdebrane")
    suspend fun getReceived(@Query("dataOd") dateStart: String, @Query("dataDo") dateEnd: String): ApiResponse<List<Message>>

    @GET("Wiadomosc.mvc/GetWiadomosciWyslane")
    suspend fun getSent(@Query("dataOd") dateStart: String, @Query("dataDo") dateEnd: String): ApiResponse<List<Message>>

    @GET("Wiadomosc.mvc/GetWiadomosciUsuniete")
    suspend fun getDeleted(@Query("dataOd") dateStart: String, @Query("dataDo") dateEnd: String): ApiResponse<List<Message>>

    @GET("Wiadomosc.mvc/GetAdresaciWiadomosci")
    suspend fun getMessageRecipients(@Query("idWiadomosci") messageId: Int): ApiResponse<List<Recipient>>

    @GET("Wiadomosc.mvc/GetRoleUzytkownika")
    suspend fun getMessageSender(@Query("idLogin") loginId: Int, @Query("idWiadomosci") messageId: Int): ApiResponse<List<Recipient>>

    @POST("Wiadomosc.mvc/GetTrescWiadomosci")
    @FormUrlEncoded
    suspend fun getMessage(
        @Field("idWiadomosc") messageId: Int,
        @Field("Folder") folderId: Int,
        @Field("Nieprzeczytana") read: Boolean,
        @Field("idWiadomoscAdresat") id: Int?
    ): ApiResponse<Message>

    @POST("NowaWiadomosc.mvc/InsertWiadomosc")
    suspend fun sendMessage(
        @Body sendMessageRequest: SendMessageRequest,
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String
    ): ApiResponse<SentMessage>

    @POST("Wiadomosc.mvc/UsunWiadomosc")
    suspend fun deleteMessage(
        @Body deleteMessageRequests: List<DeleteMessageRequest>,
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String
    ): String
}
