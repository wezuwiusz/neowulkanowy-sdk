package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Homework
import io.github.wulkanowy.sdk.pojo.HomeworkAttachment
import io.github.wulkanowy.sdk.hebe.models.Homework as HebeHomework
import io.github.wulkanowy.sdk.scrapper.homework.Homework as ScrapperHomework

@JvmName("mapScrapperHomework")
internal fun List<ScrapperHomework>.mapHomework() = map {
    Homework(
        date = it.date.toLocalDate(),
        teacher = it.teacher,
        teacherSymbol = it.teacherSymbol,
        content = it.content,
        subject = it.subject,
        entryDate = it.entryDate.toLocalDate(),
        attachments = it._attachments.map { (url, name) ->
            HomeworkAttachment(url, name)
        },
    )
}

@JvmName("mapHebeHomework")
internal fun List<HebeHomework>.mapHomework() = map {
    Homework(
        date = it.date.date,
        teacher = it.creator.displayName,
        teacherSymbol = it.creator.name
            .first()
            .toString() + it.creator.surname
            .first()
            .toString(),
        content = it.content,
        subject = it.subject.name,
        entryDate = it.dateCreated.date,
        attachments = emptyList(),
    )
}
