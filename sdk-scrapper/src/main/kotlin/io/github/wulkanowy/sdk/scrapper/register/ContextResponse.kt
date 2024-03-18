package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class ContextResponse(
    @SerialName("uczniowie")
    val students: List<ContextStudent>,
)

@Serializable
internal data class ContextStudent(
    @SerialName("config")
    val config: ContextConfig,
    @Serializable(with = CustomDateAdapter::class)
    @SerialName("dziennikDataOd")
    val registerDateFrom: LocalDateTime,
    @Serializable(with = CustomDateAdapter::class)
    @SerialName("dziennikDataDo")
    val registerDateTo: LocalDateTime,
    @SerialName("globalKeySkrzynka")
    val globalKeyMailbox: String,
    @SerialName("idDziennik")
    val registerId: Int,
    @SerialName("is13")
    val is13: Boolean,
    @SerialName("isArtystyczna")
    val isArtystyczna: Boolean,
    @SerialName("isArtystyczna13")
    val isArtystyczna13: Boolean,
    @SerialName("isDorosli")
    val isAdults: Boolean,
    @SerialName("isPolicealna")
    val isPolicealna: Boolean,
    @SerialName("isPrzedszkolak")
    val isPrzedszkolak: Boolean,
    @SerialName("isSpecjalna")
    val isSpecjalna: Boolean,
    @SerialName("isUczen")
    val isStudent: Boolean,
    @SerialName("isWychowanek")
    val isWychowanek: Boolean,
    @SerialName("jednostka")
    val schoolName: String,
    @SerialName("jednostkaGodzinaOd")
    val schoolHourFrom: String?,
    @SerialName("jednostkaGodzinaDo")
    val schoolHourTo: String?,
    @SerialName("key")
    val key: String,
    @SerialName("oddzial")
    val className: String,
    @SerialName("opiekunUcznia")
    val opiekunUcznia: Boolean,
    @SerialName("pelnoletniUczen")
    val isAdultStudent: Boolean,
    @SerialName("posiadaPesel")
    val hasPesel: Boolean,
    @SerialName("rodzajDziennika")
    val registerType: Int,
    @SerialName("uczen")
    val studentName: String,
    @SerialName("wymagaAutoryzacji")
    val isAuthorizationRequired: Boolean,
)

@Serializable
internal data class ContextConfig(
    @SerialName("isDydaktyka")
    val isDydaktyka: Boolean,
    @SerialName("isJadlospis")
    val isJadlospis: Boolean,
    @SerialName("isLekcjeZaplanowane")
    val isLekcjeZaplanowane: Boolean,
    @SerialName("isLekcjeZrealizowane")
    val showCompletedLessons: Boolean,
    @SerialName("isNadzorPedagogiczny")
    val isNadzorPedagogiczny: Boolean,
    @SerialName("isOffice365")
    val isOffice365: Boolean,
    @SerialName("isOplaty")
    val isOplaty: Boolean,
    @SerialName("isPlatnosci")
    val isPlatnosci: Boolean,
    @SerialName("isPodreczniki")
    val isPodreczniki: Boolean,
    @SerialName("isScalanieKont")
    val isScalanieKont: Boolean,
    @SerialName("isSynchronizacjaEsb")
    val isSynchronizacjaEsb: Boolean,
    @SerialName("isZaplac")
    val isZaplac: Boolean,
    @SerialName("isZglaszanieNieobecnosci")
    val isZglaszanieNieobecnosci: Boolean,
    @SerialName("isZmianaZdjecia")
    val isZmianaZdjecia: Boolean,
    @SerialName("oneDriveClientId")
    val oneDriveClientId: String,
    @SerialName("payByNetUrlForPayment")
    val payByNetUrlForPayment: String,
    @SerialName("projectClient")
    val projectClient: String?,
)
