package io.github.wulkanowy.sdk.scrapper

internal object ApiEndpoints : IApiEndpoints {

    var currentVersion = 58698

    private val endpoints
        get() = when (currentVersion) {
            58698 -> ApiEndpoints_24_4_3_58698
            in 58666..58697 -> ApiEndpoints_24_4_2_58666
            else -> ApiEndpoints_24_4_1_58566
        }
    override val UczenCache get() = endpoints.UczenCache
    override val UczenDziennik get() = endpoints.UczenDziennik
    override val PlusContext get() = endpoints.PlusContext
    override val PlusOkresyKlasyfikacyjne get() = endpoints.PlusOkresyKlasyfikacyjne
}

internal interface IApiEndpoints {
    val UczenCache: String
    val UczenDziennik: String
    val PlusContext: String
    val PlusOkresyKlasyfikacyjne: String
}

internal object ApiEndpoints_24_4_1_58566 : IApiEndpoints {
    override val UczenCache = "UczenCache"
    override val UczenDziennik = "UczenDziennik"

    // uczenplus
    override val PlusContext = "Context"
    override val PlusOkresyKlasyfikacyjne = "OkresyKlasyfikacyjne"
}

internal object ApiEndpoints_24_4_2_58666 : IApiEndpoints {
    override val UczenCache = "f18ddca0-400e-47cc-89a1-4bbe6685810b"
    override val UczenDziennik = "bef3daf1-07cd-4de6-b059-ee5909a7beb9"
    override val PlusContext = "7fbcc3fc-1021-444e-86ec-506683e02337"
    override val PlusOkresyKlasyfikacyjne = "0669f1fd-e6f0-4007-ba4a-1d99c9107bb4"
}

internal object ApiEndpoints_24_4_3_58698 : IApiEndpoints {
    override val UczenCache = "21a5186d-2aab-4123-bad7-269aa7173bb2"
    override val UczenDziennik = "a01ea13f-14f0-4c56-8b91-790e5aeecdf1"

    // uczenplus
    override val PlusContext = "Context"
    override val PlusOkresyKlasyfikacyjne = "OkresyKlasyfikacyjne"
}
