package io.github.wulkanowy.api.timetable

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class TimetableParser {

    private companion object {
        const val CLASS_PLANNED = "x-treelabel-ppl"
        const val CLASS_REALIZED = "x-treelabel-rlz"
        const val CLASS_CHANGES = "x-treelabel-zas"
        const val CLASS_MOVED_OR_CANCELED = "x-treelabel-inv"
    }

    fun getTimetable(c: TimetableResponse.TimetableRow.TimetableCell): Timetable? {
        return addLessonDetails(Timetable(c.number, c.start, c.end, c.date), c.td.select("div"))
    }

    private fun addLessonDetails(lesson: Timetable, divs: Elements): Timetable? {
        moveWarningToLessonNode(divs)

        return when {
            divs.size == 1 -> getLessonInfo(lesson, divs[0])
            divs.size == 2 && divs[1]?.selectFirst("span")?.hasClass(CLASS_MOVED_OR_CANCELED) ?: false -> {
                when {
                    divs[1]?.selectFirst("span")?.hasClass(CLASS_PLANNED) == true -> getLessonInfo(lesson, divs[0]).run {
                        val old = getLessonInfo(lesson, divs[1])
                        copy(
                            changes = true,
                            subjectOld = old.subject,
                            teacherOld = old.teacher,
                            roomOld = old.room,
                            info = stripLessonInfo("${getFormattedLessonInfo(info)}, ${old.info}").replace("$subject ", "").capitalize()
                        )
                    }
                    else -> getLessonInfo(lesson, divs[1])
                }
            }
            divs.size == 2 && divs[1]?.selectFirst("span")?.hasClass(CLASS_CHANGES) == true -> getLessonInfo(lesson, divs[1]).run {
                val old = getLessonInfo(lesson, divs[0])
                copy(
                    changes = true,
                    canceled = false,
                    subjectOld = old.subject,
                    teacherOld = old.teacher,
                    roomOld = old.room
                )
            }
            divs.size == 2 && divs[0]?.selectFirst("span")?.hasClass(CLASS_MOVED_OR_CANCELED) == true &&
                divs[0]?.selectFirst("span")?.hasClass(CLASS_PLANNED) == true &&
                divs[1]?.selectFirst("span")?.attr("class")?.isEmpty() == true -> {
                getLessonInfo(lesson, divs[1]).run {
                    val old = getLessonInfo(lesson, divs[0])
                    copy(
                        changes = true,
                        canceled = false,
                        subjectOld = old.subject,
                        teacherOld = old.teacher,
                        roomOld = old.room,
                        info = "Poprzednio: ${old.subject} (${old.info})"
                    )
                }
            }
            divs.size == 2 -> getLessonInfo(lesson, divs[0])
            divs.size == 3 -> getLessonInfo(lesson, divs[1])
            else -> null
        }
    }

    private fun moveWarningToLessonNode(e: Elements) {
        e.select(".uwaga-panel").run {
            if (!isEmpty()) {
                val original = e.select("span").last().text().removeSurrounding("(", ")")
                e.select("span").last().addClass(CLASS_REALIZED).text("($original: ${text()})")
                e.removeAt(e.size - 1)
            }
        }
    }

    private fun getLessonInfo(lesson: Timetable, div: Element): Timetable {
        div.select("span").run {
            return when {
                size == 3 -> getSimpleLesson(lesson, this)
                size == 4 && last().hasClass(CLASS_REALIZED) -> getSimpleLesson(lesson, this)
                size == 4 -> getGroupLesson(lesson, this)
                size == 5 && last().hasClass(CLASS_REALIZED) -> getGroupLesson(lesson, this)
                size == 7 -> getSimpleLessonWithReplacement(lesson, this)
                size == 9 -> getGroupLessonWithReplacement(lesson, this)
                else -> lesson
            }
        }
    }

    private fun getSimpleLesson(lesson: Timetable, spans: Elements): Timetable {
        return getLesson(lesson, spans)
    }

    private fun getSimpleLessonWithReplacement(lesson: Timetable, spans: Elements): Timetable {
        return getLessonWithReplacement(lesson, spans)
    }

    private fun getGroupLesson(lesson: Timetable, spans: Elements): Timetable {
        return getLesson(lesson, spans, 1)
    }

    private fun getGroupLessonWithReplacement(lesson: Timetable, spans: Elements): Timetable {
        return getLessonWithReplacement(lesson, spans, 1)
    }

    private fun getLesson(lesson: Timetable, spans: Elements, o: Int = 0): Timetable {
        return lesson.copy(
            subject = getLessonAndGroupInfoFromSpan(spans[0])[0],
            group = getLessonAndGroupInfoFromSpan(spans[0])[1],
            teacher = spans[1 + o].text(),
            room = spans[2 + o].text(),
            info = getFormattedLessonInfo(spans.getOrNull(3 + o)?.text()),
            canceled = spans.first().hasClass(CLASS_MOVED_OR_CANCELED),
            changes = spans.first().hasClass(CLASS_CHANGES)
        )
    }

    private fun getLessonWithReplacement(lesson: Timetable, spans: Elements, o: Int = 0): Timetable {
        return lesson.copy(
            subject = getLessonAndGroupInfoFromSpan(spans[3 + o])[0],
            subjectOld = getLessonAndGroupInfoFromSpan(spans[0])[0],
            group = getLessonAndGroupInfoFromSpan(spans[3 + o])[1],
            teacher = spans[4 + o * 2].text(),
            teacherOld = spans[1 + o].text(),
            room = spans[5 + o * 2].text(),
            roomOld = spans[2 + o].text(),
            info = "${getFormattedLessonInfo(spans.last().text())}, poprzednio: ${getLessonAndGroupInfoFromSpan(spans[0])[0]}",
            changes = true
        )
    }

    private fun getFormattedLessonInfo(info: String?): String {
        return info?.removeSurrounding("(", ")") ?: ""
    }

    private fun stripLessonInfo(info: String): String {
        return info
            .replace("okienko dla uczniów", "")
            .replace("zmiana organizacji zajęć", "")
            .replace(" ,", "")
            .removePrefix(", ")
    }

    private fun getLessonAndGroupInfoFromSpan(span: Element): Array<String> {
        return span.text().run {
            arrayOf(
                span.text().substringBefore(" ["),
                if (this.contains("[")) span.text().split(" [").last().removeSuffix("]") else ""
            )
        }
    }
}
