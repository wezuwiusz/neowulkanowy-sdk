package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.interceptor.MessagesModuleHost
import io.github.wulkanowy.sdk.scrapper.interceptor.StudentModuleHost
import io.github.wulkanowy.sdk.scrapper.interceptor.StudentPlusModuleHost
import io.github.wulkanowy.sdk.scrapper.login.ModuleHeaders
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.messages.Mailbox
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.RecipientType
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import retrofit2.Response
import java.security.MessageDigest
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter.ofPattern
import java.util.Date
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.roundToInt
import kotlin.random.Random

private val logger = LoggerFactory.getLogger("Utils")

internal fun String.toDate(format: String): Date = SimpleDateFormat(format).parse(this)

internal fun String.toLocalDate(format: String): LocalDate = LocalDate.parse(this, ofPattern(format))

internal fun Date.toLocalDate(): LocalDate = ofEpochMilli(time).atZone(systemDefault()).toLocalDate()

internal fun LocalDate.toFormat(format: String): String = format(ofPattern(format))

internal fun LocalDateTime.toFormat(format: String): String = format(ofPattern(format))

internal fun LocalDate.getSchoolYear(): Int = if (month.value > 8) year else year - 1

internal fun getGradeShortValue(value: String?): String {
    return when (value?.trim()) {
        "celujący" -> "6"
        "bardzo dobry" -> "5"
        "dobry" -> "4"
        "dostateczny" -> "3"
        "dopuszczający" -> "2"
        "niedostateczny" -> "1"
        else -> value.orEmpty().trim()
    }
}

internal fun String.getEmptyIfDash(): String {
    return when {
        this == "-" -> ""
        else -> this
    }
}

internal fun String.getGradePointPercent(): String {
    return split("/").let { (student, max) ->
        if (max.toDouble() == 0.0) return this
        "${(student.toDouble() / max.toDouble() * 100).roundToInt()}%"
    }
}

internal fun getScriptParam(name: String, content: String, fallback: String = ""): String {
    return "$name: '(.)*'".toRegex().find(content).let { result ->
        if (null !== result) Jsoup.parse(result.groupValues[0].substringAfter("'").substringBefore("'")).text() else fallback
    }
}

internal fun getScriptFlag(name: String, content: String, fallback: Boolean = false): Boolean {
    return "$name: (false|true)".toRegex().find(content).let { result ->
        if (null !== result) result.groupValues[1].toBoolean() else fallback
    }
}

fun String.getNormalizedSymbol(): String = this
    .trim().lowercase()
    .replace("default", "")
    .run {
        Normalizer.normalize(this, Normalizer.Form.NFD).run {
            "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(this, "")
        }
    }
    .replace("ł", "l")
    .replace("[^a-z0-9]".toRegex(), "")
    .ifBlank { "Default" }

internal fun List<Recipient>.normalizeRecipients() = map { it.parseName() }

internal fun Recipient.parseName(): Recipient {
    val typeSeparatorPosition = fullName.indexOfAny(RecipientType.entries.map { " - ${it.letter} - " })

    if (typeSeparatorPosition == -1) return copy(userName = fullName)

    val userName = fullName.substring(0..typeSeparatorPosition).trim()
    val typeLetter = fullName.substring(typeSeparatorPosition..typeSeparatorPosition + 3 * 2 + 1).substringAfter(" - ").substringBefore(" - ")
    val studentName = fullName.substringAfter(" - $typeLetter - ").substringBefore(" - (")
    val schoolName = fullName.substringAfter("(").trimEnd(')')
    return copy(
        userName = userName,
        studentName = studentName.takeIf { it != "($schoolName)" } ?: userName,
        type = typeLetter.let { letter -> RecipientType.entries.first { it.letter == letter } },
        schoolNameShort = schoolName,
    )
}

internal fun Mailbox.toRecipient() = Recipient(
    mailboxGlobalKey = globalKey,
    type = type,
    fullName = fullName,
    userName = userName,
    studentName = studentName,
    schoolNameShort = schoolNameShort,
)

internal fun Recipient.toMailbox() = Mailbox(
    globalKey = mailboxGlobalKey,
    userType = -1,
    type = type,
    fullName = fullName,
    userName = userName,
    studentName = studentName,
    schoolNameShort = schoolNameShort,
)

internal fun String.capitalise() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

internal val defaultUserAgentTemplate = buildString {
    append("Mozilla/5.0 (Linux; Android %1\$s; %2\$s) ")
    append("AppleWebKit/%3\$s (KHTML, like Gecko) ")
    append("Chrome/%4\$s Mobile ")
    append("Safari/%5\$s")
}

internal fun getFormattedString(
    template: String,
    androidVersion: String,
    buildTag: String,
    webKitRev: String = "537.36",
    chromeRev: String = "120.0.0.0",
): String {
    return String.format(template, androidVersion, buildTag, webKitRev, chromeRev, webKitRev)
}

internal fun isCurrentLoginHasEduOne(studentModuleUrls: List<String>, urlGenerator: UrlGenerator): Boolean {
    return studentModuleUrls.any {
        it.startsWith(
            prefix = urlGenerator.generate(UrlGenerator.Site.STUDENT_PLUS),
            ignoreCase = true,
        )
    }
}

@OptIn(ExperimentalEncodingApi::class)
internal fun getEncodedKey(studentId: Int, diaryId: Int, unitId: Int): String {
    return Base64.encode("$studentId-$diaryId-1-$unitId".toByteArray())
}

@OptIn(ExperimentalEncodingApi::class)
internal fun getDecodedKey(key: String): StudentKey {
    val decoded = Base64.decode(key).decodeToString()
    val parts = decoded.split("-").map { it.toIntOrNull() }

    if (parts.any { it == null }) {
        logger.error("Decoded student key: $decoded")
    }

    return StudentKey(
        studentId = parts.getOrNull(0) ?: Random.nextInt(),
        diaryId = parts.getOrNull(1) ?: Random.nextInt(),
        unknown = parts.getOrNull(2) ?: Random.nextInt(),
        unitId = parts.getOrNull(3) ?: Random.nextInt(),
    )
}

internal data class StudentKey(
    val studentId: Int,
    val diaryId: Int,
    val unknown: Int,
    val unitId: Int,
)

internal fun <T> Response<T>.handleErrors(): Response<T> {
    if (!isSuccessful) {
        throw HttpException(this)
    }
    return this
}

@OptIn(ExperimentalStdlibApi::class)
internal fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.toHexString()
}

internal fun HttpUrl.mapModuleUrl(moduleHost: String, appVersion: String?): HttpUrl {
    val pathSegmentIndex = getPathIndexByModuleHost(moduleHost)
    val pathKey = pathSegments.getOrNull(pathSegmentIndex)
    val mappedPath = Scrapper.endpointsMap[appVersion]
        ?.get(moduleHost)
        ?.get(pathKey?.substringBefore(".mvc"))

    return if (mappedPath != null) {
        newBuilder().setPathSegment(
            index = pathSegmentIndex,
            pathSegment = when {
                ".mvc" in pathKey.orEmpty() -> "$mappedPath.mvc"
                else -> mappedPath
            },
        ).build()
    } else {
        this
    }
}

internal fun isAnyMappingAvailable(url: String): Boolean {
    val host = url.toHttpUrl().host
    val module = when {
        MessagesModuleHost in host -> MessagesModuleHost
        StudentPlusModuleHost in host -> StudentPlusModuleHost
        StudentModuleHost in host -> StudentModuleHost
        else -> null
    } ?: return false

    return Scrapper.endpointsMap.keys.any { appVersion ->
        url.toHttpUrl().mapModuleUrl(module, appVersion).toString() !== url
    }
}

internal fun getPathIndexByModuleHost(moduleHost: String): Int = when (moduleHost) {
    StudentPlusModuleHost -> 3
    StudentModuleHost, MessagesModuleHost -> 2
    else -> -1
}

private val vParamsRegex = "([a-zA-Z]+)\\s*:\\s*'([^']*)'".toRegex()

internal fun getModuleHeadersFromDocument(htmlContent: String): ModuleHeaders {
    val matches = vParamsRegex.findAll(htmlContent)
    return ModuleHeaders(
        token = getScriptParam("antiForgeryToken", htmlContent),
        appGuid = getScriptParam("appGuid", htmlContent),
        appVersion = getScriptParam("version", htmlContent).ifBlank {
            getScriptParam("appVersion", htmlContent)
        },
        email = getScriptParam("name", htmlContent),
        symbol = getScriptParam("appCustomerDb", htmlContent),
        vParams = matches.toList().associate { match ->
            if (match.groupValues.size == 3) {
                match.groupValues[1] to match.groupValues[2]
            } else {
                null to null
            }
        },
    )
}

internal fun Request.Builder.attachVToken(moduleHost: String, url: HttpUrl, headers: ModuleHeaders?): Request.Builder {
    val vToken = url.getMatchedVToken(moduleHost, headers) ?: return this
    addHeader("V-Token", vToken)
    return this
}

internal fun HttpUrl.getMatchedVToken(moduleHost: String, headers: ModuleHeaders?): String? {
    val pathSegmentIndex = getPathIndexByModuleHost(moduleHost)
    val pathKey = pathSegments.getOrNull(pathSegmentIndex)
    val mappedUuid = Scrapper.vTokenMap[headers?.appVersion]
        ?.get(moduleHost)
        ?.get(pathKey)
        ?: return null

    return getVToken(mappedUuid, headers, moduleHost)
}

private val vTokenSchemeKeysRegex = "\\{([^{}]+)\\}".toRegex()

private fun getVToken(uuid: String, headers: ModuleHeaders?, moduleHost: String): String? {
    if (uuid.isBlank()) return null

    val scheme = Scrapper.vTokenSchemeMap[headers?.appVersion]
        ?.get(moduleHost)
        ?: "{UUID}-{appCustomerDb}-{appVersion}"
    val schemeToSubstitute = scheme.replace("{UUID}", uuid)

    val vTokenEncoded = runCatching {
        vTokenSchemeKeysRegex.replace(schemeToSubstitute) {
            val key = it.groupValues[1]
            headers?.vParams.orEmpty()[key] ?: key
        }
    }.onFailure {
        logger.error("Error preparing vtoken!", it)
    }.getOrDefault(
        schemeToSubstitute
            .replace("{appCustomerDb}", headers?.symbol.orEmpty())
            .replace("{appVersion}", headers?.appVersion.orEmpty()),
    )

    return vTokenEncoded.md5()
}
