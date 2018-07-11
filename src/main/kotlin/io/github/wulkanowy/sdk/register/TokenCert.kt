package io.github.wulkanowy.sdk.register

import com.google.gson.annotations.SerializedName

data class TokenCert(

        @SerializedName("CertyfikatKlucz")
        val certificateKey: String,

        @SerializedName("CertyfikatKluczSformatowanyTekst")
        val certificateKeyFormatted: String,

        @SerializedName("CertyfikatDataUtworzenia")
        val certificateCreatedDate: Int,

        @SerializedName("CertyfikatDataUtworzeniaSformatowanyTekst")
        val certificateCreatedDateText: String,

        @SerializedName("CertyfikatPfx")
        val certificatePfx: String,

        @SerializedName("GrupaKlientow")
        val symbol: String,

        @SerializedName("AdresBazowyRestApi")
        val apiEndpoint: String,

        @SerializedName("UzytkownikLogin")
        val userLogin: String,

        @SerializedName("UzytkownikNazwa")
        val userName: String,

        @SerializedName("TypKonta")
        val accountType: String?
)
