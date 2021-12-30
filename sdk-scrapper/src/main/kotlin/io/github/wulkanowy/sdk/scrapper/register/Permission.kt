package io.github.wulkanowy.sdk.scrapper.register

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Permission(

    @Json(name = "AuthInfos")
    val authInfos: List<AuthInfo>,

    @Json(name = "Units")
    val units: List<Unit>
)

@JsonClass(generateAdapter = true)
data class AuthInfo(
    @Json(name = "JednostkaSprawozdawczaId")
    val unitId: Int,

    @Json(name = "LinkedAccountUids")
    val linkedAccountUids: List<Int>,

    @Json(name = "LoginId")
    val loginId: Int,

    @Json(name = "LoginValue")
    val loginValue: String,

    @Json(name = "OpiekunIds")
    val parentIds: List<Int>,

    @Json(name = "PracownikIds")
    val employeeIds: List<Int>,

    @Json(name = "Roles")
    val roles: List<Int>,

    @Json(name = "UczenIds")
    val studentIds: List<Int>
)

@JsonClass(generateAdapter = true)
data class Unit(
    @Json(name = "Id")
    val id: Int,

    @Json(name = "Nazwa")
    val name: String,

    @Json(name = "Skrot")
    val short: String,

    @Json(name = "Symbol")
    val symbol: String
)
