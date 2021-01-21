package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.ApiResponse
import io.github.wulkanowy.sdk.scrapper.messages.DeleteMessageRequest
import io.github.wulkanowy.sdk.scrapper.messages.Message
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.RecipientsRequest
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
import retrofit2.http.Url

interface MessagesService {

    @GET(".")
    suspend fun getStart(): String

    @GET("NowaWiadomosc.mvc/GetJednostkiUzytkownika")
    suspend fun getUserReportingUnits(): ApiResponse<List<ReportingUnit>>

    @GET
    suspend fun getUserReportingUnits(@Url url: String): ApiResponse<List<ReportingUnit>>

    @POST("Adresaci.mvc/GetAddressee")
    suspend fun getRecipients(@Body recipientsRequest: RecipientsRequest): ApiResponse<List<Recipient>>

    @GET("Wiadomosc.mvc/GetInboxMessages")
    suspend fun getReceived(@Query("dataOd") dateStart: String, @Query("dataDo") dateEnd: String): ApiResponse<List<Message>>

    @GET("Wiadomosc.mvc/GetOutboxMessages")
    suspend fun getSent(@Query("dataOd") dateStart: String, @Query("dataDo") dateEnd: String): ApiResponse<List<Message>>

    @GET("Wiadomosc.mvc/GetTrashboxMessages")
    suspend fun getDeleted(@Query("dataOd") dateStart: String, @Query("dataDo") dateEnd: String): ApiResponse<List<Message>>

    @GET("Wiadomosc.mvc/GetMessageAddressee")
    suspend fun getMessageRecipients(@Query("idWiadomosci") messageId: Int): ApiResponse<List<Recipient>>

    @GET("Wiadomosc.mvc/GetMessageSenderRoles")
    suspend fun getMessageSender(@Query("idLogin") loginId: Int, @Query("idWiadomosci") messageId: Int): ApiResponse<List<Recipient>>

    @POST("Wiadomosc.mvc/GetInboxMessageDetails")
    @FormUrlEncoded
    suspend fun getInboxMessage(@Field("messageId") messageId: Int): ApiResponse<Message>

    @POST("Wiadomosc.mvc/GetOutboxMessageDetails")
    @FormUrlEncoded
    suspend fun getOutboxMessage(@Field("messageId") messageId: Int): ApiResponse<Message>

    @POST("Wiadomosc.mvc/GetTrashboxMessageDetails")
    @FormUrlEncoded
    suspend fun getTrashboxMessage(@Field("messageId") messageId: Int): ApiResponse<Message>

    @POST("NowaWiadomosc.mvc/InsertWiadomosc")
    suspend fun sendMessage(
        @Body sendMessageRequest: SendMessageRequest,
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String
    ): ApiResponse<SentMessage>

    @POST("Wiadomosc.mvc/DeleteInboxMessages")
    suspend fun deleteInboxMessage(
        @Body deleteMessageRequest: DeleteMessageRequest,
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String
    ): ApiResponse<Any>

    @POST("Wiadomosc.mvc/DeleteOutboxMessages")
    suspend fun deleteOutboxMessage(
        @Body deleteMessageRequest: DeleteMessageRequest,
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String
    ): ApiResponse<Any>

    @POST("Wiadomosc.mvc/DeleteTrashboxMessages")
    suspend fun deleteTrashMessages(
        @Body deleteMessageRequest: DeleteMessageRequest,
        @Header("X-V-RequestVerificationToken") token: String,
        @Header("X-V-AppGuid") appGuid: String,
        @Header("X-V-AppVersion") appVersion: String
    ): ApiResponse<Any>
}
