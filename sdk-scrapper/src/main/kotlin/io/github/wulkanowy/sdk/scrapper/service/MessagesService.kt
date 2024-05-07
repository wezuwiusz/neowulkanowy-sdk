package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.ApiEndpoints
import io.github.wulkanowy.sdk.scrapper.messages.Mailbox
import io.github.wulkanowy.sdk.scrapper.messages.MessageDetails
import io.github.wulkanowy.sdk.scrapper.messages.MessageMeta
import io.github.wulkanowy.sdk.scrapper.messages.MessageReplayDetails
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

internal interface MessagesService {

    @GET("LoginEndpoint.aspx")
    suspend fun getModuleStart(): String

    @POST
    @FormUrlEncoded
    suspend fun sendModuleCertificate(
        @Header("Referer") referer: String,
        @Url url: String,
        @FieldMap certificate: Map<String, String>,
    ): String

    @GET("api/{path}")
    suspend fun getMailboxes(@Path("path") path: String = ApiEndpoints.Skrzynki): List<Mailbox>

    @GET("api/Pracownicy")
    suspend fun getRecipients(@Query("globalKeySkrzynka") mailboxKey: String): List<Recipient>

    @GET("api/{path}")
    suspend fun getReceived(
        @Path("path") path: String = ApiEndpoints.Odebrane,
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/{path}")
    suspend fun getReceivedMailbox(
        @Path("path") path: String = ApiEndpoints.OdebraneSkrzynka,
        @Query("globalKeySkrzynka") mailboxKey: String,
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/{path}")
    suspend fun getSent(
        @Path("path") path: String = ApiEndpoints.Wyslane,
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/{path}")
    suspend fun getSentMailbox(
        @Path("path") path: String = ApiEndpoints.WyslaneSkrzynka,
        @Query("globalKeySkrzynka") mailboxKey: String,
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/{path}")
    suspend fun getDeleted(
        @Path("path") path: String = ApiEndpoints.Usuniete,
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/{path}")
    suspend fun getDeletedMailbox(
        @Path("path") path: String = ApiEndpoints.UsunieteSkrzynka,
        @Query("globalKeySkrzynka") mailboxKey: String,
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/WiadomoscSzczegoly")
    suspend fun getMessageDetails(@Query("apiGlobalKey") globalKey: String): MessageDetails?

    @PUT("api/WiadomoscSzczegoly")
    suspend fun markMessageAsRead(@Body body: Map<String, String>)

    @GET("api/{path}")
    suspend fun getMessageReplayDetails(
        @Path("path") path: String = ApiEndpoints.WiadomoscOdpowiedzPrzekaz,
        @Query("apiGlobalKey") globalKey: String,
    ): MessageReplayDetails

    @POST("api/WiadomoscNowa")
    suspend fun sendMessage(@Body body: SendMessageRequest)

    @POST("api/{path}")
    suspend fun moveMessageToTrash(
        @Body body: List<String>,
        @Path("path") path: String = ApiEndpoints.MoveTrash,
    )

    @POST("api/{path}")
    suspend fun restoreFromTrash(
        @Body body: List<String>,
        @Path("path") path: String = ApiEndpoints.RestoreTrash,
    )

    @POST("api/Delete")
    suspend fun deleteMessage(@Body body: List<String>)
}
