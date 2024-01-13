package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.messages.Mailbox
import io.github.wulkanowy.sdk.scrapper.messages.MessageDetails
import io.github.wulkanowy.sdk.scrapper.messages.MessageMeta
import io.github.wulkanowy.sdk.scrapper.messages.MessageReplayDetails
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.SendMessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

internal interface MessagesService {

    @GET("api/Skrzynki")
    suspend fun getMailboxes(): List<Mailbox>

    @GET("api/Pracownicy")
    suspend fun getRecipients(@Query("globalKeySkrzynka") mailboxKey: String): List<Recipient>

    @GET("api/Odebrane")
    suspend fun getReceived(
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/OdebraneSkrzynka")
    suspend fun getReceivedMailbox(
        @Query("globalKeySkrzynka") mailboxKey: String,
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/Wyslane")
    suspend fun getSent(
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/WyslaneSkrzynka")
    suspend fun getSentMailbox(
        @Query("globalKeySkrzynka") mailboxKey: String,
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/Usuniete")
    suspend fun getDeleted(
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/UsunieteSkrzynka")
    suspend fun getDeletedMailbox(
        @Query("globalKeySkrzynka") mailboxKey: String,
        @Query("idLastWiadomosc") lastMessageKey: Int = 0,
        @Query("pageSize") pageSize: Int = 50,
    ): List<MessageMeta>

    @GET("api/WiadomoscSzczegoly")
    suspend fun getMessageDetails(@Query("apiGlobalKey") globalKey: String): MessageDetails

    @PUT("api/WiadomoscSzczegoly")
    suspend fun markMessageAsRead(@Body body: Map<String, String>)

    @GET("api/WiadomoscOdpowiedzPrzekaz")
    suspend fun getMessageReplayDetails(@Query("apiGlobalKey") globalKey: String): MessageReplayDetails

    @POST("api/WiadomoscNowa")
    suspend fun sendMessage(@Body body: SendMessageRequest): Response<Unit>

    @POST("api/MoveTrash")
    suspend fun moveMessageToTrash(@Body body: List<String>): Response<Unit>

    @POST("api/Delete")
    suspend fun deleteMessage(@Body body: List<String>): Response<Unit>
}
