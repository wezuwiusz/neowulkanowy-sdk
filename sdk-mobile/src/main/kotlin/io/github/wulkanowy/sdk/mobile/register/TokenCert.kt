package io.github.wulkanowy.sdk.mobile.register

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenCert(

    @Json(name = "CertyfikatKlucz")
    val certificateKey: String,

    @Json(name = "CertyfikatKluczSformatowanyTekst")
    val certificateKeyFormatted: String,

    @Json(name = "CertyfikatDataUtworzenia")
    val certificateCreatedDate: Int,

    @Json(name = "CertyfikatDataUtworzeniaSformatowanyTekst")
    val certificateCreatedDateText: String,

    @Json(name = "CertyfikatPfx")
    val certificatePfx: String,

    @Json(name = "GrupaKlientow")
    val symbol: String,

    @Json(name = "AdresBazowyRestApi")
    val baseUrl: String,

    @Json(name = "UzytkownikLogin")
    val userLogin: String,

    @Json(name = "UzytkownikNazwa")
    val userName: String,

    @Json(name = "TypKonta")
    val accountType: String?,
)
