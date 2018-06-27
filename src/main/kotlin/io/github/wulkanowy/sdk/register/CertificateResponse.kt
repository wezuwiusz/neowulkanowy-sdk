package io.github.wulkanowy.sdk.register

import com.google.gson.annotations.SerializedName

data class CertificateResponse(

    @SerializedName("IsError")
    var isError: Boolean,

    @SerializedName("IsMessageForUser")
    var isMessageForUser: Boolean,

    @SerializedName("Message")
    var message: String?,

    @SerializedName("TokenKey")
    var tokenKey: String?,

    @SerializedName("TokenStatus")
    var tokenStatus: String,

    @SerializedName("TokenCert")
    var tokenCert: TokenCert
) {

    data class TokenCert(

            @SerializedName("CertyfikatKlucz")
            var certificateKey: String,

            @SerializedName("CertyfikatKluczSformatowanyTekst")
            var certificateKeyFormatted: String,

            @SerializedName("CertyfikatDataUtworzenia")
            var certificateCreatedDate: Int,

            @SerializedName("CertyfikatDataUtworzeniaSformatowanyTekst")
            var certificateCreatedDateText: String,

            @SerializedName("CertyfikatPfx")
            var certificatePfx: String,

            @SerializedName("GrupaKlientow")
            var symbol: String,

            @SerializedName("AdresBazowyRestApi")
            var apiEndpoint: String,

            @SerializedName("UzytkownikLogin")
            var userLogin: String,

            @SerializedName("UzytkownikNazwa")
            var userName: String,

            @SerializedName("TypKonta")
            var accountType: String?
    )
}
