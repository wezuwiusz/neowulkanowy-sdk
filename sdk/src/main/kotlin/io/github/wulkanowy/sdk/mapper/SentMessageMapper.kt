package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.messages.Message
import io.github.wulkanowy.sdk.pojo.Sender
import io.github.wulkanowy.sdk.pojo.SentMessage
import io.github.wulkanowy.sdk.scrapper.messages.SentMessage as ScrapperSentMessage

fun ScrapperSentMessage.mapSentMessage(): SentMessage {
    return SentMessage(
        recipients = recipients.mapRecipients(),
        id = id,
        content = content,
        isWelcomeMessage = isWelcomeMessage,
        sender = Sender(
            id = sender.id,
            role = sender.role,
            reportingUnitId = sender.reportingUnitId,
            name = sender.name,
            loginId = sender.loginId,
            hash = sender.hash
        ),
        subject = subject
    )
}

fun Message.mapSentMessage(loginId: Int): SentMessage {
    return SentMessage(
        recipients = recipients.orEmpty().mapFromMobileToRecipients(),
        subject = subject,
        content = content,
        sender = Sender(senderId.toString(), senderName, loginId, -1, -2, "-3"),
        id = messageId,
        isWelcomeMessage = false
    )
}
