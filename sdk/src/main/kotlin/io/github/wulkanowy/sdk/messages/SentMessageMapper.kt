package io.github.wulkanowy.sdk.messages

import io.github.wulkanowy.sdk.pojo.Sender
import io.github.wulkanowy.sdk.pojo.SentMessage
import io.github.wulkanowy.api.messages.SentMessage as ScrapperSentMessage

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
