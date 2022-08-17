package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.messages.Mailbox
import io.github.wulkanowy.sdk.scrapper.messages.Message
import io.github.wulkanowy.sdk.scrapper.messages.MessageMeta
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MessagesService {

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

    @GET("api/WiadomoscOdpowiedzPrzekaz")
    suspend fun getMessageDetails(@Query("apiGlobalKey") globalKey: String): Message

    @POST("api/WiadomoscNowa")
    suspend fun sendMessage(@Body sendMessageRequest: SendMessageRequest)

    @POST("api/MoveTrash")
    suspend fun deleteMessage(@Body body: List<String>)
}
