package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.interceptor.MessagesModuleHost
import io.github.wulkanowy.sdk.scrapper.interceptor.StudentModuleHost
import io.github.wulkanowy.sdk.scrapper.interceptor.StudentPlusModuleHost
import io.github.wulkanowy.sdk.scrapper.login.ModuleHeaders
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import io.github.wulkanowy.sdk.scrapper.messages.Mailbox
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.RecipientType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
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

internal fun getScriptParamTabValues(name: String, content: String): List<String> {
    val tab = "var $name = \\[(.*?)\\];".toRegex(RegexOption.DOT_MATCHES_ALL).find(content).let { result ->
        result?.groupValues?.firstOrNull()
    }.orEmpty()
    return "'([A-F0-9-]{36})'".toRegex().findAll(tab).toList().mapNotNull {
        it.groupValues.lastOrNull()
    }
}

internal fun getApiKey(document: Document, fallback: String = ""): String {
    val scripts = document.getElementsByTag("script").toList().map { it.html() }
    val script = scripts.lastOrNull { "VParam" in it }

    if (script == null) {
        return fallback
    }

    return "'([a-zA-Z0-9]{8,9})'".toRegex().findAll(script).lastOrNull().let { result ->
        if (null !== result) result.groupValues[1] else fallback
    }
}

private fun getSymbolSig(symbol: String): String {
    if (symbol.isBlank()) return ""
    return "${symbol.first()}${symbol.length}"
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
    append("Mozilla/5.0 (Linux; Androidx %1\$s; %2\$s) ")
    append("AppleWebKit/%3\$s (KHTML, like Gecko) ")
    append("Chrome/%4\$s Mobile ")
    append("Safari/%5\$s")
}

internal fun getFormattedString(
    template: String,
    androidVersion: String,
    buildTag: String,
    webKitRev: String = "537.36",
    chromeRev: String = "125.0.0.0",
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
    val mappedPath = (Scrapper.endpointsMap[appVersion] ?: ApiEndpointsMap[appVersion])
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
        val pathKeySub = pathSegments.getOrNull(pathSegmentIndex + 1)
        val pathConcatenated = "$pathKey/$pathKeySub"
        val mappedPathWithSub = (Scrapper.endpointsMap[appVersion] ?: ApiEndpointsMap[appVersion])
            ?.get(moduleHost)
            ?.get(pathConcatenated)

        if (mappedPathWithSub != null) {
            val pathSplit = mappedPathWithSub.split("/")
            newBuilder()
                .setPathSegment(pathSegmentIndex, pathSplit[0])
                .setPathSegment(pathSegmentIndex + 1, pathSplit[1])
                .build()
        } else {
            this
        }
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

internal suspend fun getModuleHeadersFromDocument(document: Document): ModuleHeaders {
    val htmlContent = document.select("script").html()
    val matches = vParamsRegex.findAll(htmlContent)

    val scripts = document.select("script").toList()
        .filter { it.attr("src").isNullOrBlank() }
        .map { it.html() }

    val evaluatedJs = runBlocking {
        Scrapper.vParamsRun().use { handler ->
            scripts.map {
                runCatching {
                    val result = handler.evaluate("var window = this; $it; JSON.stringify(VParam)")
                    result?.let { Json.parseToJsonElement(it) }?.jsonObject?.toMap().orEmpty()
                }.getOrDefault(emptyMap())
            }
        }
    }.flatMap { it.toList() }.toMap()
        .mapValues {
            when (it.value) {
                is JsonPrimitive -> it.value.jsonPrimitive.content
                else -> it.value.toString()
            }
        }

    return ModuleHeaders(
        token = getScriptParam("antiForgeryToken", htmlContent),
        appGuid = getScriptParam("appGuid", htmlContent),
        appVersion = getScriptParam("version", htmlContent).ifBlank {
            getScriptParam("appVersion", htmlContent)
        },
        email = getScriptParam("name", htmlContent),
        symbol = getScriptParam("appCustomerDb", htmlContent),
        vParamsRaw = matches.toList().associate { match ->
            if (match.groupValues.size == 3) {
                match.groupValues[1] to match.groupValues[2]
            } else {
                null to null
            }
        } + mapOf(
            "apiKey" to getApiKey(document),
            "appCustomerDbSig" to getSymbolSig(getScriptParam("appCustomerDb", htmlContent)),
        ),
        vParamsEvaluated = evaluatedJs,
        vApiTokens = getScriptParamTabValues("VApiKeys", htmlContent),
    )
}

internal fun getModuleHost(url: HttpUrl): String {
    return when {
        MessagesModuleHost in url.host -> MessagesModuleHost
        StudentPlusModuleHost in url.host -> StudentPlusModuleHost
        StudentModuleHost in url.host -> StudentModuleHost
        else -> ""
    }
}

internal fun getVHeaders(moduleHost: String, url: HttpUrl, headers: ModuleHeaders?): Map<String, String> {
    val vHeaders = Scrapper.vHeadersMap[headers?.appVersion] ?: ApiEndpointsVHeaders[headers?.appVersion]

    return vHeaders?.get(moduleHost).orEmpty().mapNotNull { (key, scheme) ->
        val headerValue = url.getMatchedVHeader(
            moduleHost = moduleHost,
            domainSchema = scheme,
            headers = headers,
        )
        when {
            headerValue != null -> key to headerValue
            else -> null
        }
    }.toMap()
}

private fun HttpUrl.getMatchedVHeader(moduleHost: String, domainSchema: String?, headers: ModuleHeaders?): String? {
    val pathSegmentIndex = getPathIndexByModuleHost(moduleHost)
    val pathKey = pathSegments.getOrNull(pathSegmentIndex)
    val mappedSimpleUuid = (Scrapper.vTokenMap[headers?.appVersion] ?: ApiEndpointsVTokenMap[headers?.appVersion])
        ?.get(moduleHost)
        ?.get(pathKey)

    val mappedUuid = if (mappedSimpleUuid == null) {
        val pathKeySub = pathSegments.getOrNull(pathSegmentIndex + 1)
        (Scrapper.vTokenMap[headers?.appVersion] ?: ApiEndpointsVTokenMap[headers?.appVersion])
            ?.get(moduleHost)
            ?.get("$pathKey/$pathKeySub")
    } else {
        mappedSimpleUuid
    }

    return getVToken(
        uuid = mappedUuid ?: return null,
        headers = headers,
        domainSchema = domainSchema,
    )
}

private val vTokenSchemeKeysRegex = "\\{([^{}]+)\\}".toRegex()

private fun getVToken(uuid: String, headers: ModuleHeaders?, domainSchema: String?): String? {
    if (uuid.isBlank()) return null

    val schemeToSubstitute = domainSchema ?: "{UUID}-{appCustomerDb}-{appCustomerDbSig}-{appVersion}-{apiKey}"

    val vTokenEncoded = runCatching {
        vTokenSchemeKeysRegex.replace(schemeToSubstitute) {
            val key = it.groupValues[1]
            val headersRaw = headers?.vParamsRaw.orEmpty()[key]
            val headerEvaluated = headers?.vParamsEvaluated.orEmpty()[key]
            headerEvaluated ?: headersRaw ?: "{$key}"
        }
    }.onFailure {
        logger.error("Error preparing vToken!", it)
    }.getOrDefault(
        schemeToSubstitute
            .replace("{appCustomerDb}", headers?.symbol.orEmpty())
            .replace("{appVersion}", headers?.appVersion.orEmpty())
            .replace("{email}", headers?.email.orEmpty()),
    )

    val withSubstitutions = vTokenEncoded.replace("{UUID}", uuid)

    if (withSubstitutions == schemeToSubstitute) {
        val lastVTokenDigitSum = headers?.vApiTokens?.lastOrNull().orEmpty().sumOf {
            if (it.isDigit()) it.digitToInt() else 0
        }
        val vTokenIndex = lastVTokenDigitSum % ((headers?.vApiTokens?.size ?: 0) - 1)

        return vTokenEncoded
            .replace("{%UUID%}", uuid)
            .replace("{%vTokenApiIndexed%}", headers?.vApiTokens?.get(vTokenIndex).orEmpty())
    }

    return withSubstitutions.md5()
}
